package lab.ui;

import dsd.sequential.counters.AsynchronousBinaryCounter;
import dsd.sequential.counters.BcdCounter;
import dsd.sequential.counters.Counter;
import dsd.sequential.counters.SynchronousBinaryCounter;
import dsd.sequential.counters.UpDownCounter;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Asynchronous, synchronous, up/down and BCD counters, with a ripple animation and a count-sequence table. */
final class CounterLab implements Lab {

    private final ComboBox<String> type = Kit.combo("Asynchronous (ripple) up counter", "Synchronous up counter", "Up / Down counter", "BCD (decade) counter");
    private final ComboBox<String> size = Kit.combo("2 bits", "3 bits", "4 bits");
    private Counter counter;
    private final VBox cells = new VBox(10);
    private final HBox bitRow = new HBox(10);
    private final Label[] names = new Label[4];
    private BitCells bits;
    private final Label value = Kit.plain("", "card-title");
    private final Label note = Kit.label("", "status-info");
    private final VBox tableHolder = new VBox();
    private final WaveView[] wave = new WaveView[1];
    private TruthGrid sequence;
    private final Timeline auto;
    private Timeline ripple;
    private boolean up = true;
    private final Label directionLabel = Kit.plain("Direction: counting UP", "lab-title");
    private final HBox directionBox;
    private int width;

    CounterLab() {
        size.setValue("3 bits");
        directionBox = new HBox(10, directionLabel, Kit.button("⇅ Change direction", "btn-secondary", e -> {
            if (!(counter instanceof UpDownCounter)) return;
            up = !up;
            ((UpDownCounter) counter).setCountUp(up);
            directionLabel.setText("Direction: counting " + (up ? "UP" : "DOWN"));
        }));
        directionBox.setAlignment(Pos.CENTER_LEFT);
        type.setOnAction(e -> rebuild());
        size.setOnAction(e -> rebuild());
        auto = new Timeline(new KeyFrame(Duration.millis(1400), e -> tick()));
        auto.setCycleCount(Animation.INDEFINITE);
        rebuild();
    }

    private void rebuild() {
        auto.stop();
        if (ripple != null) ripple.stop();
        int idx = type.getSelectionModel().getSelectedIndex();
        width = 2 + size.getSelectionModel().getSelectedIndex();
        up = true;
        directionLabel.setText("Direction: counting UP");
        counter = switch (idx) {
            case 0 -> new AsynchronousBinaryCounter(width);
            case 1 -> new SynchronousBinaryCounter(width);
            case 2 -> new UpDownCounter(width);
            default -> new BcdCounter();
        };
        if (idx == 3) width = 4;
        size.setDisable(idx == 3);
        directionBox.setVisible(idx == 2);
        directionBox.setManaged(idx == 2);
        bits = new BitCells(width, false);
        bitRow.getChildren().clear();
        for (int i = width - 1; i >= 0; i--) {
            Label n = Kit.plain("Q" + i, "reg-value");
            n.setMinWidth(38);
            n.setAlignment(Pos.CENTER);
            bitRow.getChildren().add(n);
        }
        cells.getChildren().setAll(bitRow, bits);
        List<String> names = new ArrayList<>();
        names.add("CLK");
        for (int i = width - 1; i >= 0; i--) names.add("Q" + i);
        wave[0] = new WaveView(names);
        buildSequence();
        show(false);
        note.setText(switch (idx) {
            case 0 -> "Asynchronous: each flip-flop is clocked by the previous one, so the change ripples from Q0 upwards with a small delay.";
            case 1 -> "Synchronous: every flip-flop gets the same clock and they all change at the same moment.";
            case 2 -> "Up/Down: the direction control chooses whether the count rises or falls.";
            default -> "BCD counter: counts 0 to 9 and then returns to 0 (the states 10 - 15 are skipped).";
        });
    }

    private void buildSequence() {
        List<String[]> rows = new ArrayList<>();
        Counter probe = switch (type.getSelectionModel().getSelectedIndex()) {
            case 0 -> new AsynchronousBinaryCounter(width);
            case 1 -> new SynchronousBinaryCounter(width);
            case 2 -> new UpDownCounter(width);
            default -> new BcdCounter();
        };
        int states = type.getSelectionModel().getSelectedIndex() == 3 ? 10 : 1 << width;
        for (int i = 0; i <= states; i++) {
            StringBuilder b = new StringBuilder();
            for (boolean x : probe.getState().getBits()) b.append(x ? '1' : '0');
            rows.add(new String[] {String.valueOf(i == states ? 0 : i), b.toString(), String.valueOf(probe.getValue()),
                i == states ? "wraps around" : ""});
            probe.clockTick();
        }
        rows.remove(rows.size() - 1);
        sequence = new TruthGrid(List.of("Pulse", "Q" + (width - 1) + "…Q0", "Decimal", ""), 2, rows);
        tableHolder.getChildren().setAll(sequence);
    }

    private String bitString() {
        StringBuilder b = new StringBuilder();
        for (boolean x : counter.getState().getBits()) b.append(x ? '1' : '0');
        return b.toString();
    }

    private void show(boolean animate) {
        bits.set(bitString(), animate);
        value.setText("Count = " + counter.getValue());
        sequence.highlight(counter.getValue() >= sequence.rowCount() ? -1 : counter.getValue());
    }

    private void tick() {
        String before = bitString();
        counter.clockTick();
        String after = bitString();
        boolean[] sample = new boolean[width + 1];
        sample[0] = true;
        for (int i = 0; i < width; i++) sample[i + 1] = after.charAt(i) == '1';
        wave[0].push(sample);
        value.setText("Count = " + counter.getValue());
        sequence.highlight(counter.getValue() >= sequence.rowCount() ? -1 : counter.getValue());
        if (type.getSelectionModel().getSelectedIndex() == 0) {
            // ripple: the changed bits flip one after another starting with the least significant
            if (ripple != null) ripple.stop();
            ripple = new Timeline();
            StringBuilder shown = new StringBuilder(before);
            int step = 0;
            for (int pos = width - 1; pos >= 0; pos--) {
                if (before.charAt(pos) != after.charAt(pos)) {
                    final int p = pos;
                    step++;
                    ripple.getKeyFrames().add(new KeyFrame(Duration.millis(260 * step), e -> {
                        shown.setCharAt(p, after.charAt(p));
                        bits.set(shown.toString(), true);
                    }));
                }
            }
            if (step == 0) bits.set(after, false); else ripple.play();
        } else {
            bits.set(after, true);
        }
    }

    @Override
    public Node diagram() {
        return Kit.label("An n-bit counter built from n flip-flops has 2ⁿ states. In a ripple (asynchronous) counter, flip-flop 0 toggles on every clock pulse, flip-flop 1 toggles every second pulse, flip-flop 2 every fourth pulse and so on, so each stage halves the frequency of the one before. "
            + "A synchronous counter uses extra gates so every flip-flop changes together. A BCD counter resets itself after 9.", "body-text");
    }

    @Override
    public Node table() { return tableHolder; }

    @Override
    public Node interactive() {
        HBox pick = new HBox(10, type, size);
        pick.setAlignment(Pos.CENTER_LEFT);
        HBox buttons = new HBox(10, Kit.button("⏱ Clock pulse", "btn-primary", e -> tick()),
            Kit.button("▶ Auto clock", "btn-secondary", e -> { if (auto.getStatus() == Animation.Status.RUNNING) auto.stop(); else auto.play(); }),
            Kit.button("Reset to 0", "btn-secondary", e -> { auto.stop(); counter.reset(); wave[0].clear(); show(false); }));
        VBox waveHolder = new VBox();
        waveHolder.getChildren().add(wave[0]);
        type.valueProperty().addListener((o, a, b) -> waveHolder.getChildren().setAll(wave[0]));
        size.valueProperty().addListener((o, a, b) -> waveHolder.getChildren().setAll(wave[0]));
        return new VBox(14, Kit.label("Choose a counter and press Clock pulse (or Auto clock). Bits that change flash yellow.", "muted"), pick, directionBox, cells, value, buttons, note,
            Kit.plain("Timing diagram", "lab-title"), waveHolder);
    }

    @Override
    public void dispose() {
        auto.stop();
        if (ripple != null) ripple.stop();
    }
}
