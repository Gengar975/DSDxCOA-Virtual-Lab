package lab.ui;

import dsd.booleanlogic.BooleanExpressionParser;
import dsd.booleanlogic.TruthTable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Boolean expressions and truth tables: type an expression, get the table, SOP/POS forms and live evaluation. */
final class ExprLab implements Lab {

    private final TextField input = Kit.field("e.g.  A & !B | C", 320);
    private final Label status = Kit.plain("", "status-info");
    private final Label live = Kit.plain("", "formula");
    private final VBox tableBox = new VBox(10);
    private final VBox toggleBox = new VBox(8);
    private final HBox toggles = new HBox(10);
    private final Label forms = Kit.label("", "formula");
    private TruthGrid grid;
    private String expression = "";
    private List<String> vars = new ArrayList<>();
    private final List<ToggleButton> toggleButtons = new ArrayList<>();

    ExprLab() {
        input.setText("A & !B | C");
        input.setOnAction(e -> build());
        build();
    }

    private void build() {
        String text = input.getText().trim();
        try {
            TruthTable t = new TruthTable(text);
            expression = text;
            vars = new ArrayList<>(t.getVariableNames());
            List<String> headers = new ArrayList<>(vars);
            headers.add("Y");
            List<String[]> rows = new ArrayList<>();
            for (int r = 0; r < t.getInputRows().size(); r++) {
                boolean[] in = t.getInputRows().get(r);
                List<String> row = new ArrayList<>();
                for (boolean v : in) row.add(v ? "1" : "0");
                row.add(t.getOutputValues().get(r) ? "1" : "0");
                rows.add(row.toArray(new String[0]));
            }
            grid = new TruthGrid(headers, 1, rows);
            grid.reveal(90);
            tableBox.getChildren().setAll(grid);
            forms.setText("Minterms (rows where Y = 1):  " + t.getMinterms() + "\nMaxterms (rows where Y = 0):  " + t.getMaxterms()
                    + "\nSum of products:      Y = " + t.toSopExpression() + "\nProduct of sums:      Y = " + t.toPosExpression());
            status.setText("✔ " + (1 << vars.size()) + " rows for " + vars.size() + " variables");
            status.getStyleClass().setAll("status-ok");
            toggleButtons.clear();
            toggles.getChildren().clear();
            for (String v : vars) {
                ToggleButton tb = Kit.bitToggle(v, e -> evaluateLive());
                toggleButtons.add(tb);
                toggles.getChildren().add(tb);
            }
            evaluateLive();
        } catch (IllegalArgumentException ex) {
            status.setText("✖ " + ex.getMessage() + "   (use A-Z, & for AND, | for OR, ! for NOT, ^ for XOR, parentheses)");
            status.getStyleClass().setAll("status-bad");
        }
    }

    private void evaluateLive() {
        Map<String, Boolean> values = new LinkedHashMap<>();
        int row = 0;
        for (int i = 0; i < vars.size(); i++) {
            boolean v = toggleButtons.get(i).isSelected();
            values.put(vars.get(i), v);
            row = (row << 1) | (v ? 1 : 0);
        }
        boolean y = BooleanExpressionParser.evaluate(expression, values);
        grid.highlight(row);
        StringBuilder sb = new StringBuilder("With ");
        for (String v : vars) sb.append(v).append('=').append(values.get(v) ? 1 : 0).append(' ');
        sb.append("→  Y = ").append(y ? 1 : 0).append("   (row ").append(row).append(" of the table)");
        live.setText(sb.toString());
    }

    @Override
    public Node diagram() {
        List<String[]> laws = List.of(
            new String[] {"Identity", "A + 0 = A", "A · 1 = A"},
            new String[] {"Null", "A + 1 = 1", "A · 0 = 0"},
            new String[] {"Idempotent", "A + A = A", "A · A = A"},
            new String[] {"Complement", "A + A' = 1", "A · A' = 0"},
            new String[] {"Double negation", "(A')' = A", ""},
            new String[] {"Commutative", "A + B = B + A", "A · B = B · A"},
            new String[] {"Distributive", "A · (B + C) = AB + AC", "A + BC = (A + B)(A + C)"},
            new String[] {"Absorption", "A + AB = A", "A · (A + B) = A"},
            new String[] {"De Morgan", "(A + B)' = A' · B'", "(A · B)' = A' + B'"});
        GridPane g = new TruthGrid(List.of("Law", "OR form", "AND form"), 0, laws);
        return new VBox(10, Kit.label("Boolean laws you can use to simplify an expression:", "body-text"), g);
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10, Kit.plain("Expression  Y =", "lab-title"), input, Kit.button("Build truth table", "btn-primary", e -> build()));
        bar.setAlignment(Pos.CENTER_LEFT);
        toggleBox.getChildren().setAll(Kit.plain("Flip the variables to evaluate the expression:", "lab-title"), toggles, live);
        return new VBox(14, Kit.label("Type any expression with the variables A, B, C, D ... Use & (AND), | (OR), ! (NOT), ^ (XOR) and brackets.", "muted"),
            bar, status, tableBox, forms, toggleBox);
    }
}
