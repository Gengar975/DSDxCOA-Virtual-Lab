package lab.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lab.content.Topic;
import lab.model.CircuitLibrary;
import lab.model.CircuitTable;
import playground.Circuit;
import playground.ComponentType;

/**
 * Lab for every topic that is drawn at gate level: the logic gates, half/full adder and
 * half/full subtractor. The diagram is a real {@link Circuit}; the truth table is produced by
 * running that circuit for every input combination.
 */
final class CircuitLab implements Lab {

    private final Topic topic;
    private final Circuit circuit;
    private final CircuitView view;
    private final CircuitTable table;
    private final TruthGrid grid;
    private final Label readout = Kit.plain("", "formula");
    private SwitchAnalogyView analogy;
    private CircuitView equivalentView;

    CircuitLab(Topic topic) {
        this.topic = topic;
        this.circuit = CircuitLibrary.forTopic(topic.getId());
        this.table = new CircuitTable(circuit);
        this.view = new CircuitView(circuit, true);
        List<String> headers = new ArrayList<>(table.getInputLabels());
        headers.addAll(table.getOutputLabels());
        List<String[]> data = new ArrayList<>();
        for (int r = 0; r < table.rowCount(); r++) {
            List<String> row = new ArrayList<>();
            for (boolean b : table.inputs(r)) row.add(b ? "1" : "0");
            for (boolean b : table.outputs(r)) row.add(b ? "1" : "0");
            data.add(row.toArray(new String[0]));
        }
        grid = new TruthGrid(headers, table.getOutputLabels().size(), data);
        view.setOnChange(this::sync);
        sync();
    }

    private void sync() {
        int row = table.rowFor(circuit);
        grid.highlight(row);
        StringBuilder sb = new StringBuilder("Current row: ");
        boolean[] in = table.inputs(row), out = table.outputs(row);
        for (int i = 0; i < in.length; i++) sb.append(table.getInputLabels().get(i)).append('=').append(in[i] ? 1 : 0).append("  ");
        sb.append("→  ");
        for (int i = 0; i < out.length; i++) sb.append(table.getOutputLabels().get(i)).append('=').append(out[i] ? 1 : 0).append("  ");
        readout.setText(sb.toString());
    }

    private static ComponentType gateType(String id) {
        return switch (id) {
            case "and" -> ComponentType.AND;
            case "or" -> ComponentType.OR;
            case "not" -> ComponentType.NOT;
            case "nand" -> ComponentType.NAND;
            case "nor" -> ComponentType.NOR;
            case "xor" -> ComponentType.XOR;
            case "xnor" -> ComponentType.XNOR;
            default -> null;
        };
    }

    @Override
    public Node diagram() {
        String id = topic.getId();
        ComponentType g = gateType(id);
        VBox box = new VBox(16);
        if (g != null) {
            String expr = switch (id) {
                case "and" -> "Y = A · B";
                case "or" -> "Y = A + B";
                case "not" -> "Y = A'";
                case "nand" -> "Y = (A · B)'";
                case "nor" -> "Y = (A + B)'";
                case "xor" -> "Y = A ⊕ B";
                default -> "Y = (A ⊕ B)'";
            };
            String note = switch (id) {
                case "and" -> "1 only if all inputs are 1";
                case "or" -> "1 if any input is 1";
                case "not" -> "inverts the input";
                case "nand" -> "AND, then NOT";
                case "nor" -> "OR, then NOT";
                case "xor" -> "1 if the inputs differ";
                default -> "1 if the inputs are equal";
            };
            box.getChildren().add(new GateSymbolView(g, expr, note));
            if (id.equals("and") || id.equals("or") || id.equals("not")) {
                analogy = new SwitchAnalogyView(id);
                box.getChildren().addAll(Kit.label("Real-world picture: " + (id.equals("and")
                        ? "two switches in SERIES. The lamp lights only when both are closed."
                        : id.equals("or") ? "two switches in PARALLEL. The lamp lights when either one is closed."
                        : "a contact that opens when the input is 1, so the lamp is on when the input is 0."), "muted"), analogy);
            } else {
                Circuit eq = CircuitLibrary.equivalent(id);
                if (eq != null) {
                    equivalentView = new CircuitView(eq, true);
                    box.getChildren().addAll(Kit.label("The same function built from simpler gates (click the inputs to check that it matches):", "muted"), equivalentView);
                }
            }
        } else {
            box.getChildren().add(Kit.label(blockNote(id), "body-text"));
        }
        return box;
    }

    private static String blockNote(String id) {
        return switch (id) {
            case "half-adder" -> "Black-box view: two inputs A and B, two outputs SUM and CARRY.  SUM = A ⊕ B   CARRY = A · B.   Inside: one XOR gate and one AND gate (see the interactive circuit below).";
            case "full-adder" -> "Black-box view: three inputs A, B, CIN and two outputs SUM and COUT.  SUM = A ⊕ B ⊕ CIN   COUT = A·B + CIN·(A ⊕ B).   Inside: two half adders plus an OR gate.";
            case "half-sub" -> "Black-box view: inputs A and B, outputs DIFF and BORROW.  DIFF = A ⊕ B   BORROW = A' · B.   Inside: an XOR gate, a NOT gate and an AND gate.";
            default -> "Black-box view: inputs A, B, BIN and outputs DIFF and BOUT.  DIFF = A ⊕ B ⊕ BIN   BOUT = A'·B + (A ⊕ B)'·BIN.   Inside: two half subtractors plus an OR gate.";
        };
    }

    @Override
    public Node table() {
        return new VBox(10, grid, readout);
    }

    @Override
    public Node interactive() {
        return new VBox(12, Kit.label("Click the input switches. Watch the new values travel through the gates (amber dot), the wires turn green for 1, and the matching row of the truth table lights up.", "muted"), view);
    }

    @Override
    public void dispose() {
        view.stop();
        if (analogy != null) analogy.stop();
        if (equivalentView != null) equivalentView.stop();
    }
}
