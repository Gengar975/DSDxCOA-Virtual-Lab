package lab.ui;

import coa.CoaService;
import coa.alu.AluOperation;
import coa.alu.AluResult;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** The ALU: choose the operands and an operation, see the operands, result and the C / Z / S / V status flags. */
final class AluLab implements Lab {

    private final CoaService service = new CoaService();
    private final TextField a = Kit.field("A", 100);
    private final TextField b = Kit.field("B", 100);
    private final ComboBox<String> width = Kit.combo("4 bits", "8 bits");
    private final BitCells aBits = new BitCells(4, false);
    private final BitCells bBits = new BitCells(4, false);
    private final BitCells rBits = new BitCells(4, false);
    private final Label opLabel = Kit.plain("", "card-title");
    private final Label[] flags = new Label[4];
    private final Label status = Kit.label("", "status-info");
    private final Label steps = Kit.label("", "formula");

    AluLab() {
        a.setText("11");
        b.setText("6");
        for (int i = 0; i < 4; i++) {
            flags[i] = new Label();
            flags[i].getStyleClass().add("bit");
            flags[i].setMinWidth(120);
        }
        width.setOnAction(e -> apply(AluOperation.ADD));
        apply(AluOperation.ADD);
    }

    private void apply(AluOperation op) {
        try {
            int w = width.getSelectionModel().getSelectedIndex() == 0 ? 4 : 8;
            long x = Long.parseLong(a.getText().trim());
            long y = op == AluOperation.NOT ? 0 : Long.parseLong(b.getText().trim());
            AluResult r = op == AluOperation.NOT ? service.computeAluNot(x, w) : service.computeAluOperation(x, y, op, w);
            aBits.set(r.getOperandA(), true);
            bBits.set(op == AluOperation.NOT ? "0".repeat(w) : r.getOperandB(), true);
            rBits.set(r.getResultBinary(), true);
            String sym = switch (op) { case ADD -> "+"; case SUB -> "−"; case AND -> "AND"; case OR -> "OR"; case XOR -> "XOR"; default -> "NOT"; };
            opLabel.setText(op == AluOperation.NOT ? "NOT A" : "A " + sym + " B");
            setFlag(0, "Carry", r.isCarryFlag());
            setFlag(1, "Zero", r.isZeroFlag());
            setFlag(2, "Sign", r.isSignFlag());
            setFlag(3, "Overflow", r.isOverflowFlag());
            status.setText("");
            steps.setText(String.join("\n", r.getSteps()));
        } catch (NumberFormatException ex) {
            status.setText("✖ Please type whole numbers for A and B.");
            status.getStyleClass().setAll("status-bad");
        } catch (RuntimeException ex) {
            status.setText("✖ " + ex.getMessage());
            status.getStyleClass().setAll("status-bad");
        }
    }

    private void setFlag(int i, String name, boolean on) {
        flags[i].setText(name + " = " + (on ? 1 : 0));
        flags[i].getStyleClass().remove("bit-1");
        if (on) flags[i].getStyleClass().add("bit-1");
    }

    @Override
    public Node diagram() {
        return Kit.label("The ALU receives two operands A and B and a control code that selects the operation. It produces the result and four flags: "
            + "Carry (carry out of the top bit), Zero (result is 0), Sign (top bit of the result, 1 = negative) and Overflow (signed result does not fit).", "body-text");
    }

    @Override
    public Node interactive() {
        HBox in = new HBox(10, Kit.plain("A", "lab-title"), a, Kit.plain("B", "lab-title"), b, width);
        in.setAlignment(Pos.CENTER_LEFT);
        HBox ops = new HBox(8);
        for (AluOperation op : AluOperation.values()) ops.getChildren().add(Kit.button(op.name(), "btn-primary", e -> apply(op)));
        HBox fl = new HBox(10, flags);
        VBox rows = new VBox(8, row("A", aBits), row("B", bBits), opLabel, row("Result", rBits));
        return new VBox(14, Kit.label("Type two numbers (negative numbers use 2's complement), then press an operation button.", "muted"), in, ops, status, rows, Kit.plain("Status flags", "lab-title"), fl, steps);
    }

    private static HBox row(String name, BitCells c) {
        HBox h = new HBox(12, Kit.plain(name, "reg-name"), c);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }
}
