package lab.ui;

import coa.fpu.Ieee754Converter;
import coa.fpu.Ieee754Result;
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

/** IEEE 754 single precision: the 32 bits are filled in one by one, coloured as sign / exponent / mantissa. */
final class IeeeLab implements Lab {

    private final ComboBox<String> direction = Kit.combo("Decimal number → IEEE 754 bits", "Hex bit pattern → decimal number");
    private final TextField input = Kit.field("", 220);
    private final BitCells bits;
    private final Label status = Kit.plain("", "status-info");
    private final Label formula = Kit.label("", "formula");
    private final VBox details = new VBox(10);
    private StepPlayer player;
    private Timeline fill;

    IeeeLab() {
        String[] zones = new String[32];
        zones[0] = "bit-sign";
        for (int i = 1; i <= 8; i++) zones[i] = "bit-exp";
        for (int i = 9; i < 32; i++) zones[i] = "bit-man";
        bits = new BitCells(32, true, zones);
        bits.setDimZeros(true);
        input.setText("-6.25");
        direction.setOnAction(e -> {
            input.setText(direction.getSelectionModel().getSelectedIndex() == 0 ? "-6.25" : "C0C80000");
            run();
        });
        input.setOnAction(e -> run());
        run();
    }

    private void run() {
        if (fill != null) fill.stop();
        if (player != null) player.stop();
        details.getChildren().clear();
        try {
            Ieee754Result r;
            String text = input.getText().trim();
            if (direction.getSelectionModel().getSelectedIndex() == 0) {
                r = Ieee754Converter.toIeee754(Double.parseDouble(text));
            } else {
                String hex = text.toLowerCase().startsWith("0x") ? text.substring(2) : text;
                r = Ieee754Converter.fromIeee754((int) Long.parseLong(hex, 16));
            }
            String pattern = String.format("%32s", Integer.toBinaryString(r.getBitPattern())).replace(' ', '0');
            bits.set("0".repeat(32), false);
            fill = new Timeline();
            for (int i = 0; i < 32; i++) {
                final int k = i;
                fill.getKeyFrames().add(new KeyFrame(Duration.millis(55 * (i + 1)), e ->
                    bits.set(pattern.substring(0, k + 1) + "0".repeat(31 - k), true)));
            }
            fill.play();
            int e = Integer.parseInt(pattern.substring(1, 9), 2);
            formula.setText("Value = (-1)^" + r.getSign() + "  ×  1." + r.getMantissaBits().replaceAll("0+$", "") + "₂  ×  2^(" + e + " - " + r.getBiasedExponentBias() + ")"
                    + "\n      = " + (r.getSign() == 1 ? "-" : "+") + "1.xxx × 2^" + r.getRawExponent() + "          Category: " + r.getCategory()
                    + "\nHex: " + r.getHexString() + "     Stored exponent E = " + e + "     Actual exponent = " + r.getRawExponent());
            player = new StepPlayer(r.getSteps(), 190);
            details.getChildren().addAll(Kit.plain("How the number is converted:", "lab-title"), player);
            status.setText("");
        } catch (RuntimeException ex) {
            status.setText("✖ " + (ex instanceof NumberFormatException ? "Please type a valid number (or hex digits for the pattern)." : ex.getMessage()));
            status.getStyleClass().setAll("status-bad");
        }
    }

    @Override
    public Node diagram() {
        HBox key = new HBox(10);
        key.setAlignment(Pos.CENTER_LEFT);
        Label s = Kit.plain("1 bit SIGN (0 = positive, 1 = negative)", "bit-sign");
        Label ex = Kit.plain("8 bits EXPONENT (bias 127)", "bit-exp");
        Label m = Kit.plain("23 bits MANTISSA (fraction after the leading 1)", "bit-man");
        for (Label l : new Label[] {s, ex, m}) {
            l.getStyleClass().add("bit");
            l.setMinWidth(0);
            l.setStyle("-fx-font-size: 12px; -fx-padding: 6 12 6 12;");
            key.getChildren().add(l);
        }
        return new VBox(12, Kit.label("A 32-bit single-precision number is split into three fields. Value = (-1)^sign × 1.mantissa × 2^(exponent − 127).", "body-text"), key);
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10, direction, input, Kit.button("Convert", "btn-primary", e -> run()));
        bar.setAlignment(Pos.CENTER_LEFT);
        HBox heads = new HBox(0);
        Label a = Kit.plain("S", "reg-value"), b = Kit.plain("Exponent (8 bits)", "reg-value"), c = Kit.plain("Mantissa (23 bits)", "reg-value");
        a.setMinWidth(30); b.setMinWidth(240); c.setMinWidth(300);
        heads.getChildren().addAll(a, b, c);
        return new VBox(14, Kit.label("Type a decimal number such as 6.25, -0.1 or 100 and watch the 32 bits being filled in.", "muted"), bar, status, heads, bits, formula, details);
    }

    @Override
    public void dispose() {
        if (fill != null) fill.stop();
        if (player != null) player.stop();
    }
}
