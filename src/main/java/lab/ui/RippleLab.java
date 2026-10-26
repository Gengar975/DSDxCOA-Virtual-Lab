package lab.ui;

import dsd.combinational.AluArithmeticResult;
import dsd.combinational.FullAdder;
import dsd.combinational.RippleCarryAdderSubtractor;
import java.util.Map;
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
 * 4-bit ripple carry adder / subtractor. Four full-adder boxes are drawn side by side; after a
 * change the carry visibly ripples from the rightmost stage (bit 0) to the left.
 */
final class RippleLab implements Lab {

    private static final int N = 4;

    private final ToggleButton[] a = new ToggleButton[N];
    private final ToggleButton[] b = new ToggleButton[N];
    private final ToggleButton sub;
    private final Label[] stageTitle = new Label[N];
    private final Label[] stageIn = new Label[N];
    private final Label[] stageOut = new Label[N];
    private final VBox[] stage = new VBox[N];
    private final Label[] carryArrow = new Label[N + 1];
    private final Label summary = Kit.label("", "formula");
    private final BitCells sumBits = new BitCells(N, false);
    private Timeline running;

    RippleLab() {
        sub = Kit.bitToggle("SUB", e -> recompute());
        for (int i = N - 1; i >= 0; i--) {
            final int bit = i;
            a[i] = Kit.bitToggle("A" + i, e -> recompute());
            b[i] = Kit.bitToggle("B" + i, e -> recompute());
            stageTitle[bit] = Kit.plain("FA" + bit, "lab-title");
            stageIn[bit] = Kit.plain("", "formula");
            stageOut[bit] = Kit.plain("", "formula");
            stage[bit] = new VBox(4, stageTitle[bit], stageIn[bit], stageOut[bit]);
            stage[bit].getStyleClass().add("lab-panel");
            stage[bit].setAlignment(Pos.CENTER);
            stage[bit].setMinWidth(150);
        }
        for (int i = 0; i <= N; i++) carryArrow[i] = Kit.plain("", "lab-title");
        recompute();
    }

    private static boolean bit(ToggleButton t) { return t.isSelected(); }

    private void recompute() {
        if (running != null) running.stop();
        boolean subtract = bit(sub);
        boolean[] aBits = new boolean[N], bBits = new boolean[N];   // index 0 = most significant (engine order)
        for (int i = 0; i < N; i++) {
            aBits[i] = bit(a[N - 1 - i]);
            bBits[i] = bit(b[N - 1 - i]);
        }
        AluArithmeticResult whole = new RippleCarryAdderSubtractor().compute(aBits, bBits, subtract);
        // stage by stage with the real FullAdder, so the student sees each carry
        boolean carry = subtract;
        boolean[] cIn = new boolean[N], sum = new boolean[N], cOut = new boolean[N], bUsed = new boolean[N];
        for (int i = 0; i < N; i++) {
            boolean ai = bit(a[i]);
            boolean bi = bit(b[i]) ^ subtract;
            Map<String, Boolean> r = new FullAdder().compute(Map.of("A", ai, "B", bi, "CIN", carry));
            cIn[i] = carry;
            bUsed[i] = bi;
            sum[i] = r.get("SUM");
            cOut[i] = r.get("COUT");
            carry = cOut[i];
        }
        for (int i = 0; i < N; i++) {
            stage[i].getStyleClass().remove("callout");
            stageIn[i].setText("A=" + (bit(a[i]) ? 1 : 0) + " B=" + (bUsed[i] ? 1 : 0) + " Cin=" + (cIn[i] ? 1 : 0));
            stageOut[i].setText("");
            carryArrow[i].setText("");
        }
        final boolean[] fs = sum, fc = cOut, fcin = cIn;
        running = new Timeline();
        for (int i = 0; i < N; i++) {
            final int s = i;
            running.getKeyFrames().add(new KeyFrame(Duration.millis(450 * (i + 1)), e -> {
                for (int k = 0; k < N; k++) stage[k].getStyleClass().remove("callout");
                stage[s].getStyleClass().add("callout");
                stageOut[s].setText("SUM=" + (fs[s] ? 1 : 0) + " Cout=" + (fc[s] ? 1 : 0));
                carryArrow[s + 1].setText("carry " + (fc[s] ? 1 : 0) + " ◀");
                String bits = "";
                for (int k = N - 1; k >= 0; k--) bits += k <= s ? (fs[k] ? "1" : "0") : "·";
                sumBits.set(bits.replace('·', '0'), true);
            }));
        }
        running.getKeyFrames().add(new KeyFrame(Duration.millis(450 * (N + 1)), e -> {
            for (int k = 0; k < N; k++) stage[k].getStyleClass().remove("callout");
            sumBits.set(whole.getBinaryString(), false);
            int av = 0, bv = 0;
            for (int k = N - 1; k >= 0; k--) { av = av * 2 + (bit(a[k]) ? 1 : 0); bv = bv * 2 + (bit(b[k]) ? 1 : 0); }
            summary.setText((bit(sub) ? av + " - " + bv : av + " + " + bv) + " = " + whole.getBinaryString() + "   |   carry out = " + (whole.isCarryFlag() ? 1 : 0)
                    + "   zero = " + (whole.isZeroFlag() ? 1 : 0) + "   sign = " + (whole.isSignFlag() ? 1 : 0) + "   overflow = " + (whole.isOverflowFlag() ? 1 : 0));
        }));
        summary.setText("Rippling…");
        running.play();
    }

    @Override
    public Node diagram() {
        Label text = Kit.label("Four full adders in a row. The carry-out of each stage feeds the carry-in of the next, so the carry ripples from bit 0 (right) to bit 3 (left). "
                + "For subtraction (SUB = 1) every bit of B is flipped by an XOR gate and the first carry-in is 1, which adds the 2's complement of B.", "body-text");
        return text;
    }

    @Override
    public Node interactive() {
        HBox aRow = new HBox(8), bRow = new HBox(8), stages = new HBox(10);
        for (int i = N - 1; i >= 0; i--) { aRow.getChildren().add(a[i]); bRow.getChildren().add(b[i]); }
        for (int i = N - 1; i >= 0; i--) {
            stages.getChildren().add(stage[i]);
            if (i > 0) stages.getChildren().add(carryArrow[i]);
        }
        stages.setAlignment(Pos.CENTER_LEFT);
        return new VBox(14,
            Kit.label("Set the bits of A and B (and SUB for subtraction). Watch the carry ripple through the four stages.", "muted"),
            Kit.row(14, Kit.plain("A", "lab-title"), aRow), Kit.row(14, Kit.plain("B", "lab-title"), bRow), sub,
            stages, Kit.row(12, Kit.plain("Result", "lab-title"), sumBits), summary);
    }

    @Override
    public void dispose() {
        if (running != null) running.stop();
    }
}
