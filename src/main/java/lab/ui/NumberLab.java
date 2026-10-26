package lab.ui;

import dsd.numbers.ConversionResult;
import dsd.numbers.NumberConverter;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Number systems and codes: base conversion, Gray code, BCD / Excess-3 and ASCII, each with step-by-step animation. */
final class NumberLab implements Lab {

    private final String mode;
    private final TextField input = Kit.field("", 220);
    private final ComboBox<String> kind;
    private final VBox output = new VBox(14);
    private final Label status = Kit.plain("", "status-info");
    private StepPlayer player;
    private Timeline reveal;

    NumberLab(String mode) {
        this.mode = mode;
        switch (mode) {
            case "num-base" -> {
                kind = Kit.combo("Decimal → Binary", "Decimal → Octal", "Decimal → Hexadecimal", "Binary → Decimal", "Octal → Decimal", "Hexadecimal → Decimal");
                input.setText("13");
            }
            case "num-gray" -> { kind = null; input.setText("1011"); input.setPromptText("binary, e.g. 1011"); }
            case "num-bcd" -> { kind = null; input.setText("259"); input.setPromptText("decimal, e.g. 259"); }
            default -> { kind = null; input.setText("Hi"); input.setPromptText("text, e.g. Hi"); }
        }
        if (kind != null) kind.setOnAction(e -> {
            String k = kind.getValue();
            input.setText(k.startsWith("Decimal") ? "13" : k.startsWith("Binary") ? "1101" : k.startsWith("Octal") ? "17" : "FF");
            run();
        });
        input.setOnAction(e -> run());
        run();
    }

    private ConversionResult convert(String text) {
        return switch (mode) {
            case "num-gray" -> NumberConverter.binaryToGray(text);
            case "num-bcd" -> NumberConverter.decimalToBcd(text);
            case "num-ascii" -> NumberConverter.textToAsciiBinary(text);
            default -> switch (kind.getValue()) {
                case "Decimal → Binary" -> NumberConverter.decimalToBinary(Long.parseLong(text));
                case "Decimal → Octal" -> NumberConverter.decimalToOctal(Long.parseLong(text));
                case "Decimal → Hexadecimal" -> NumberConverter.decimalToHexadecimal(Long.parseLong(text));
                case "Binary → Decimal" -> NumberConverter.binaryToDecimal(text);
                case "Octal → Decimal" -> NumberConverter.octalToDecimal(text);
                default -> NumberConverter.hexadecimalToDecimal(text);
            };
        };
    }

    private void run() {
        if (reveal != null) reveal.stop();
        if (player != null) player.stop();
        String text = input.getText().trim();
        try {
            ConversionResult r = convert(text);
            output.getChildren().clear();
            output.getChildren().add(summary(text, r));
            switch (mode) {
                case "num-gray" -> output.getChildren().add(grayRows(text, r.getConvertedValue()));
                case "num-bcd" -> output.getChildren().add(bcdRows(text, r.getConvertedValue()));
                case "num-ascii" -> output.getChildren().add(asciiRows(text, r.getConvertedValue()));
                default -> { Node n = baseBits(r.getConvertedValue()); if (n != null) output.getChildren().add(n); }
            }
            player = new StepPlayer(r.getSteps(), Math.min(220, 44 + 34 * r.getSteps().size()));
            output.getChildren().addAll(Kit.plain("Step by step (press Play to watch the method):", "lab-title"), player);
            status.setText("");
        } catch (RuntimeException ex) {
            output.getChildren().clear();
            status.setText("✖ " + (ex instanceof NumberFormatException ? "'" + text + "' is not a valid number for this conversion." : ex.getMessage()));
            status.getStyleClass().setAll("status-bad");
        }
    }

    private Node summary(String in, ConversionResult r) {
        Label l = Kit.plain(in + "   →   " + r.getConvertedValue(), "card-title");
        return l;
    }

    /** Binary results are shown as bit cells with their place values. */
    private Node baseBits(String value) {
        if (!value.matches("[01]+") || value.length() > 16) return null;
        BitCells cells = new BitCells(value.length(), false);
        cells.set("0".repeat(value.length()), false);
        HBox weights = new HBox(6);
        weights.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < value.length(); i++) {
            Label w = Kit.plain(String.valueOf(1L << (value.length() - 1 - i)), "reg-value");
            w.setMinWidth(38);
            w.setAlignment(Pos.CENTER);
            weights.getChildren().add(w);
        }
        animateReveal(cells, value);
        return new VBox(4, Kit.plain("Bits and their place values:", "lab-title"), weights, cells);
    }

    private void animateReveal(BitCells cells, String target) {
        reveal = new Timeline();
        for (int i = 0; i < target.length(); i++) {
            final int k = i;
            reveal.getKeyFrames().add(new KeyFrame(Duration.millis(220 * (i + 1)), e ->
                cells.set(target.substring(0, k + 1) + "0".repeat(target.length() - k - 1), true)));
        }
        reveal.play();
    }

    private Node grayRows(String binary, String gray) {
        BitCells b = new BitCells(binary.length(), false);
        b.set(binary, false);
        HBox xor = new HBox(6);
        for (int i = 0; i < binary.length(); i++) {
            Label l = Kit.plain(i == 0 ? "copy" : "⊕ prev", "reg-value");
            l.setMinWidth(38);
            l.setAlignment(Pos.CENTER);
            xor.getChildren().add(l);
        }
        BitCells g = new BitCells(gray.length(), false);
        g.set("0".repeat(gray.length()), false);
        animateReveal(g, gray);
        return new VBox(6, Kit.plain("Binary", "lab-title"), b, Kit.plain("each Gray bit = this bit XOR the bit to its left:", "reg-value"), xor, Kit.plain("Gray code", "lab-title"), g);
    }

    private Node bcdRows(String digits, String bcd) {
        String[] groups = bcd.split("\\s+");
        VBox box = new VBox(10);
        HBox row = new HBox(22);
        for (int i = 0; i < groups.length; i++) {
            BitCells cells = new BitCells(4, false);
            cells.set("0000", false);
            final BitCells c = cells;
            final String target = groups[i];
            Timeline t = new Timeline(new KeyFrame(Duration.millis(500 * (i + 1)), e -> c.set(target, true)));
            t.play();
            row.getChildren().add(new VBox(4, Kit.plain("digit " + digits.charAt(Math.min(i, digits.length() - 1)), "lab-title"), cells));
        }
        box.getChildren().addAll(Kit.plain("Each decimal digit becomes its own 4-bit group:", "lab-title"), row);
        try {
            String ex3 = NumberConverter.decimalToExcess3(digits).getConvertedValue();
            box.getChildren().addAll(Kit.plain("Excess-3 code (each BCD digit + 3):  " + ex3, "formula"));
        } catch (RuntimeException ignored) { }
        return box;
    }

    private Node asciiRows(String text, String binary) {
        String[] bytes = binary.split("\\s+");
        VBox box = new VBox(8);
        for (int i = 0; i < bytes.length && i < text.length(); i++) {
            BitCells cells = new BitCells(8, true);
            cells.set("00000000", false);
            final BitCells c = cells;
            final String target = bytes[i];
            new Timeline(new KeyFrame(Duration.millis(450 * (i + 1)), e -> c.set(target, true))).play();
            Label ch = Kit.plain("'" + text.charAt(i) + "'", "reg-name");
            Label code = Kit.plain("= " + (int) text.charAt(i), "reg-value");
            HBox line = new HBox(12, ch, code, cells);
            line.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(line);
        }
        return box;
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        if (kind != null) bar.getChildren().add(kind);
        bar.getChildren().addAll(input, Kit.button("Convert", "btn-primary", e -> run()));
        return new VBox(14, Kit.label("Enter a value and press Convert (or Enter). The result is built bit by bit, and the steps can be replayed.", "muted"), bar, status, output);
    }

    @Override
    public void dispose() {
        if (reveal != null) reveal.stop();
        if (player != null) player.stop();
    }
}
