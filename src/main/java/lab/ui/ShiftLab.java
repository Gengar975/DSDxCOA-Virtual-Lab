package lab.ui;

import dsd.sequential.registers.PipoShiftRegister;
import dsd.sequential.registers.PisoShiftRegister;
import dsd.sequential.registers.ShiftRegister;
import dsd.sequential.registers.SipoShiftRegister;
import dsd.sequential.registers.SisoShiftRegister;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** SISO, SIPO, PISO and PIPO shift registers, 4 bits wide. Bits enter on the right and shift left. */
final class ShiftLab implements Lab {

    private static final int N = 4;

    private final ComboBox<String> type = Kit.combo("SISO - serial in, serial out", "SIPO - serial in, parallel out",
        "PISO - parallel in, serial out", "PIPO - parallel in, parallel out");
    private SisoShiftRegister siso;
    private SipoShiftRegister sipo;
    private PisoShiftRegister piso;
    private PipoShiftRegister pipo;
    private final BitCells register = new BitCells(N, false);
    private final ToggleButton serialIn = Kit.bitToggle("Serial in", e -> { });
    private final List<ToggleButton> parallelIn = new ArrayList<>();
    private final Label serialOut = Kit.plain("", "formula");
    private final Label history = Kit.label("", "formula");
    private final Label note = Kit.label("", "status-info");
    private final HBox serialRow = new HBox(10);
    private final HBox parallelRow = new HBox(10);
    private final HBox loadRow = new HBox(10);
    private final StringBuilder outBits = new StringBuilder();
    private Timeline slide;

    ShiftLab() {
        for (int i = 0; i < N; i++) parallelIn.add(Kit.bitToggle("P" + (N - 1 - i), e -> { }));
        serialRow.setAlignment(Pos.CENTER_LEFT);
        serialRow.getChildren().add(serialIn);
        parallelRow.setAlignment(Pos.CENTER_LEFT);
        parallelRow.getChildren().addAll(parallelIn);
        loadRow.getChildren().add(Kit.button("Load parallel data", "btn-secondary", e -> load()));
        type.setOnAction(e -> reset());
        reset();
    }

    private int mode() { return type.getSelectionModel().getSelectedIndex(); }

    private int[] parallelValues() {
        int[] v = new int[N];
        for (int i = 0; i < N; i++) v[i] = parallelIn.get(i).isSelected() ? 1 : 0;
        return v;
    }

    private ShiftRegister current() {
        return switch (mode()) {
            case 0 -> siso;
            case 1 -> sipo;
            case 2 -> piso;
            default -> pipo;
        };
    }

    private String bits() {
        StringBuilder b = new StringBuilder();
        for (boolean x : current().getState().getBits()) b.append(x ? '1' : '0');
        return b.toString();
    }

    private void reset() {
        if (slide != null) slide.stop();
        siso = new SisoShiftRegister(N);
        sipo = new SipoShiftRegister(N);
        piso = new PisoShiftRegister(N);
        pipo = new PipoShiftRegister(N);
        outBits.setLength(0);
        int m = mode();
        serialRow.setVisible(m <= 1);
        serialRow.setManaged(m <= 1);
        parallelRow.setVisible(m >= 2);
        parallelRow.setManaged(m >= 2);
        loadRow.setVisible(m >= 2);
        loadRow.setManaged(m >= 2);
        register.set(bits(), false);
        serialOut.setText("Serial out: " + (m == 1 || m == 3 ? "(not used - read all bits at once)" : "-"));
        history.setText("");
        note.setText(switch (m) {
            case 0 -> "SISO: set Serial in, press Clock. The bit enters on the right; after 4 pulses it comes out on the left.";
            case 1 -> "SIPO: feed bits in one at a time. After 4 pulses all 4 bits can be read in parallel.";
            case 2 -> "PISO: first load the parallel data, then each clock pulse pushes one bit out of the left end.";
            default -> "PIPO: load all bits at once and read them all at once; it simply stores the 4-bit word.";
        });
    }

    private void load() {
        if (mode() == 2) piso.loadParallel(parallelValues());
        else pipo.loadParallel(parallelValues());
        register.set(bits(), true);
        note.setText("Parallel data loaded into all four flip-flops at the same time.");
    }

    private void clock() {
        String before = bits();
        int m = mode();
        int in = serialIn.isSelected() ? 1 : 0;
        String out = "";
        switch (m) {
            case 0 -> out = String.valueOf(siso.clockPulse(in));
            case 1 -> sipo.clockPulse(in);
            case 2 -> out = String.valueOf(piso.clockPulse());
            default -> note.setText("A PIPO register has no shifting: use Load parallel data.");
        }
        if (m == 3) return;
        String after = bits();
        // the register content slides one place to the left, then the new bit appears on the right
        if (slide != null) slide.stop();
        String mid = before.substring(1) + "·";
        slide = new Timeline(new KeyFrame(Duration.millis(250), e -> register.set(mid.replace('·', '0'), true)),
            new KeyFrame(Duration.millis(600), e -> register.set(after, true)));
        slide.play();
        if (!out.isEmpty()) {
            outBits.append(out);
            serialOut.setText("Serial out so far: " + outBits + "   (this pulse: " + out + ")");
        }
        history.setText("Before: " + before + "   →   after the clock pulse: " + after + (m <= 1 ? "   (new bit " + in + " entered on the right)" : ""));
    }

    @Override
    public Node diagram() {
        return Kit.label("Four D flip-flops in a row, all driven by the same clock. On each pulse every flip-flop copies the value of its neighbour, so the whole word moves one place. "
            + "Serial = one bit at a time on one wire; parallel = all four bits at once on four wires.", "body-text");
    }

    @Override
    public Node interactive() {
        HBox buttons = new HBox(10, Kit.button("⏱ Clock pulse", "btn-primary", e -> clock()), Kit.button("Reset", "btn-secondary", e -> reset()));
        HBox names = new HBox(6);
        for (int i = N - 1; i >= 0; i--) {
            Label l = Kit.plain("FF" + i, "reg-value");
            l.setMinWidth(38);
            l.setAlignment(Pos.CENTER);
            names.getChildren().add(l);
        }
        return new VBox(14, Kit.label("Choose a register type, set the input bits and press Clock pulse. Watch the bits slide to the left.", "muted"), type,
            serialRow, parallelRow, loadRow, names, register, buttons, serialOut, history, note);
    }

    @Override
    public void dispose() {
        if (slide != null) slide.stop();
    }
}
