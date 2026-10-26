package lab.ui;

import dsd.sequential.components.DFlipFlop;
import dsd.sequential.components.JKFlipFlop;
import dsd.sequential.components.MasterSlaveJKFlipFlop;
import dsd.sequential.components.SRFlipFlop;
import dsd.sequential.components.TFlipFlop;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * SR, D, JK, T and master-slave JK flip-flops. The student sets the inputs and presses
 * "Clock pulse"; Q and Q' change, the characteristic table highlights the row that was used and
 * a timing diagram records the clock, the inputs and Q.
 */
final class FlipFlopLab implements Lab {

    private final String kind;   // sr, d, jk, t, ms
    private final SRFlipFlop sr = new SRFlipFlop();
    private final DFlipFlop d = new DFlipFlop();
    private final JKFlipFlop jk = new JKFlipFlop();
    private final TFlipFlop t = new TFlipFlop();
    private final MasterSlaveJKFlipFlop ms = new MasterSlaveJKFlipFlop();
    private final List<ToggleButton> inputs = new ArrayList<>();
    private final List<String> inputNames = new ArrayList<>();
    private final BitCells q = new BitCells(1, false);
    private final BitCells qBar = new BitCells(1, false);
    private final BitCells master = new BitCells(1, false);
    private final Label status = Kit.label("", "status-info");
    private final WaveView wave;
    private final TruthGrid table;
    private final BlockView symbol;
    private final Timeline auto;
    private Timeline phase;

    FlipFlopLab(String kind) {
        this.kind = kind;
        switch (kind) {
            case "sr" -> inputNames.addAll(List.of("S", "R"));
            case "d" -> inputNames.add("D");
            case "t" -> inputNames.add("T");
            default -> inputNames.addAll(List.of("J", "K"));
        }
        for (String n : inputNames) inputs.add(Kit.bitToggle(n, e -> { }));
        List<String> waveNames = new ArrayList<>();
        waveNames.add("CLK");
        waveNames.addAll(inputNames);
        waveNames.add("Q");
        wave = new WaveView(waveNames);
        List<String> pins = new ArrayList<>(inputNames);
        pins.add("CLK");
        symbol = new BlockView(title(), pins, List.of("Q", "Q'"), false, null, null);
        table = buildTable();
        auto = new Timeline(new KeyFrame(Duration.millis(1500), e -> pulse()));
        auto.setCycleCount(Animation.INDEFINITE);
        showState(false);
    }

    private String title() {
        return switch (kind) {
            case "sr" -> "SR FLIP-FLOP";
            case "d" -> "D FLIP-FLOP";
            case "jk" -> "JK FLIP-FLOP";
            case "t" -> "T FLIP-FLOP";
            default -> "MASTER-SLAVE JK";
        };
    }

    private TruthGrid buildTable() {
        List<String[]> rows = new ArrayList<>();
        List<String> head = new ArrayList<>(inputNames);
        head.add("Q(next)");
        head.add("Meaning");
        switch (kind) {
            case "sr" -> {
                rows.add(new String[] {"0", "0", "Q", "hold"});
                rows.add(new String[] {"0", "1", "0", "reset"});
                rows.add(new String[] {"1", "0", "1", "set"});
                rows.add(new String[] {"1", "1", "?", "forbidden"});
            }
            case "d" -> {
                rows.add(new String[] {"0", "0", "Q becomes 0"});
                rows.add(new String[] {"1", "1", "Q becomes 1"});
            }
            case "t" -> {
                rows.add(new String[] {"0", "Q", "hold"});
                rows.add(new String[] {"1", "Q'", "toggle"});
            }
            default -> {
                rows.add(new String[] {"0", "0", "Q", "hold"});
                rows.add(new String[] {"0", "1", "0", "reset"});
                rows.add(new String[] {"1", "0", "1", "set"});
                rows.add(new String[] {"1", "1", "Q'", "toggle"});
            }
        }
        if (kind.equals("d")) head = List.of("D", "Q(next)", "Meaning");
        if (kind.equals("t")) head = List.of("T", "Q(next)", "Meaning");
        return new TruthGrid(head, 2, rows);
    }

    private int inputValue(int i) { return inputs.get(i).isSelected() ? 1 : 0; }

    private int rowForInputs() {
        return switch (kind) {
            case "d", "t" -> inputValue(0);
            default -> inputValue(0) * 2 + inputValue(1);
        };
    }

    private void showState(boolean animate) {
        int qv, qb;
        switch (kind) {
            case "sr" -> { qv = sr.getQ(); qb = sr.getQBar(); }
            case "d" -> { qv = d.getQ(); qb = d.getQBar(); }
            case "t" -> { qv = t.getQ(); qb = t.getQBar(); }
            case "jk" -> { qv = jk.getQ(); qb = jk.getQBar(); }
            default -> { qv = ms.getQ(); qb = ms.getQBar(); }
        }
        q.set(String.valueOf(qv), animate);
        qBar.set(String.valueOf(qb), animate);
    }

    private int currentQ() {
        return switch (kind) {
            case "sr" -> sr.getQ();
            case "d" -> d.getQ();
            case "t" -> t.getQ();
            case "jk" -> jk.getQ();
            default -> ms.getQ();
        };
    }

    private void pulse() {
        if (phase != null) phase.stop();
        int row = rowForInputs();
        boolean[] sample = new boolean[inputs.size() + 2];
        sample[0] = true;
        for (int i = 0; i < inputs.size(); i++) sample[i + 1] = inputs.get(i).isSelected();
        try {
            switch (kind) {
                case "sr" -> sr.clockPulse(inputValue(0), inputValue(1));
                case "d" -> d.clockPulse(inputValue(0));
                case "t" -> t.clockPulse(inputValue(0));
                case "jk" -> jk.clockPulse(inputValue(0), inputValue(1));
                default -> {
                    // two phases: clock HIGH (master reads J, K), then clock LOW (slave copies the master)
                    ms.clockHigh(inputValue(0), inputValue(1));
                    master.set(String.valueOf(ms.getMasterQ()), true);
                    status.setText("Clock HIGH: the master latch reads J and K and becomes " + ms.getMasterQ() + ". The output Q has not changed yet.");
                    phase = new Timeline(new KeyFrame(Duration.millis(900), e -> {
                        ms.clockLow();
                        showState(true);
                        status.setText("Clock LOW: the slave copies the master, so Q = " + ms.getQ() + ".");
                    }));
                    phase.play();
                    sample[sample.length - 1] = ms.getMasterQ() == 1;
                    wave.push(sample);
                    table.highlight(row);
                    return;
                }
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            status.setText("⚠ " + ex.getMessage());
            status.getStyleClass().setAll("status-bad");
            table.highlight(row);
            return;
        }
        sample[sample.length - 1] = currentQ() == 1;
        wave.push(sample);
        showState(true);
        table.highlight(row);
        status.getStyleClass().setAll("status-info");
        status.setText("Clock pulse applied: Q is now " + currentQ() + " and Q' is " + (1 - currentQ()) + ".");
    }

    private void reset() {
        auto.stop();
        if (phase != null) phase.stop();
        sr.reset(); d.reset(); jk.reset(); t.reset(); ms.reset();
        wave.clear();
        table.highlight(-1);
        master.set("0", false);
        showState(false);
        status.setText("Flip-flop reset: Q = 0.");
    }

    @Override
    public Node diagram() {
        String text = switch (kind) {
            case "sr" -> "Two inputs: S (set) and R (reset), plus the clock. Q is the stored bit and Q' its opposite.";
            case "d" -> "One data input D plus the clock. On each clock pulse Q copies D.";
            case "t" -> "One input T plus the clock. When T = 1 the output toggles on each clock pulse.";
            case "jk" -> "Inputs J (set) and K (reset) plus the clock. J = K = 1 makes the output toggle.";
            default -> "A master latch and a slave latch in series. While CLK = 1 the master reads J and K; when CLK falls to 0 the slave copies the master, so Q changes only once per cycle.";
        };
        return new VBox(12, Kit.label(text, "body-text"), symbol);
    }

    @Override
    public Node table() { return table; }

    @Override
    public Node interactive() {
        HBox in = new HBox(10);
        in.setAlignment(Pos.CENTER_LEFT);
        in.getChildren().add(Kit.plain("Inputs", "lab-title"));
        in.getChildren().addAll(inputs);
        HBox buttons = new HBox(10, Kit.button("⏱ Clock pulse", "btn-primary", e -> pulse()),
            Kit.button("▶ Auto clock", "btn-secondary", e -> { if (auto.getStatus() == Animation.Status.RUNNING) auto.stop(); else auto.play(); }),
            Kit.button("Reset", "btn-secondary", e -> reset()));
        HBox outs = new HBox(14, Kit.plain("Q", "reg-name"), q, Kit.plain("Q'", "reg-name"), qBar);
        outs.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(14, Kit.label("Set the inputs, then press Clock pulse. The output only changes when the clock pulse arrives.", "muted"), in, buttons, outs);
        if (kind.equals("ms")) {
            HBox m = new HBox(14, Kit.plain("Master", "reg-name"), master);
            m.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(m);
        }
        box.getChildren().addAll(status, Kit.plain("Timing diagram", "lab-title"), wave);
        return box;
    }

    @Override
    public void dispose() {
        auto.stop();
        if (phase != null) phase.stop();
    }
}
