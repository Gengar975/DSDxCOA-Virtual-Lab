package lab.ui;

import coa.CoaService;
import coa.arithmetic.BoothMultiplicationResult;
import coa.arithmetic.DivisionResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lab.model.StepRegisters;

/**
 * Booth multiplication, restoring and non-restoring division. The project's algorithm classes
 * produce the steps; this screen plays them back while showing the registers A, Q, M and Q-1 as
 * rows of bit cells, so students can see which bits change in each iteration.
 */
final class ArithmeticLab implements Lab {

    private final String mode;   // booth, restoring, non-restoring
    private final CoaService service = new CoaService();
    private final TextField first;
    private final TextField second;
    private final ComboBox<String> width = Kit.combo("4 bits", "6 bits", "8 bits");
    private final Label status = Kit.plain("", "status-info");
    private final Label answer = Kit.plain("", "card-title");
    private final VBox registers = new VBox(8);
    private final Map<String, BitCells> cells = new LinkedHashMap<>();
    private final VBox bottom = new VBox(12);
    private StepPlayer player;
    private List<String> steps = List.of();

    ArithmeticLab(String mode) {
        this.mode = mode;
        boolean booth = mode.equals("booth");
        first = Kit.field(booth ? "multiplicand" : "dividend", 120);
        second = Kit.field(booth ? "multiplier" : "divisor", 120);
        first.setText(booth ? "3" : "13");
        second.setText(booth ? "-2" : "3");
        first.setOnAction(e -> run());
        second.setOnAction(e -> run());
        run();
    }

    private void run() {
        if (player != null) player.stop();
        bottom.getChildren().clear();
        registers.getChildren().clear();
        cells.clear();
        try {
            int w = 4 + 2 * width.getSelectionModel().getSelectedIndex();
            long a = Long.parseLong(first.getText().trim()), b = Long.parseLong(second.getText().trim());
            switch (mode) {
                case "booth" -> {
                    BoothMultiplicationResult r = service.computeBoothMultiplication(a, b, w);
                    steps = r.getSteps();
                    answer.setText(a + " × " + b + " = " + r.getProduct() + "     (binary " + r.getProductBinary() + ")");
                }
                case "restoring" -> {
                    DivisionResult r = service.computeRestoringDivision(a, b, w);
                    steps = r.getSteps();
                    answer.setText(a + " ÷ " + b + ":   quotient " + r.getQuotient() + ",  remainder " + r.getRemainder());
                }
                default -> {
                    DivisionResult r = service.computeNonRestoringDivision(a, b, w);
                    steps = r.getSteps();
                    answer.setText(a + " ÷ " + b + ":   quotient " + r.getQuotient() + ",  remainder " + r.getRemainder());
                }
            }
            status.setText("");
            player = new StepPlayer(steps, 260);
            player.setOnStep(this::showRegisters);
            bottom.getChildren().addAll(Kit.plain("Press Play to watch the algorithm, or step through it with Next:", "lab-title"), registers, player);
            showRegisters(-1);
        } catch (NumberFormatException ex) {
            status.setText("✖ Please type whole numbers (negative numbers are allowed).");
            status.getStyleClass().setAll("status-bad");
            answer.setText("");
        } catch (ArithmeticException ex) {
            status.setText("✖ The divisor cannot be 0 - division by zero is undefined.");
            status.getStyleClass().setAll("status-bad");
            answer.setText("");
        } catch (IllegalArgumentException ex) {
            status.setText("✖ " + ex.getMessage());
            status.getStyleClass().setAll("status-bad");
            answer.setText("");
        }
    }

    /** Rebuilds the register values as they are after step {@code upTo}. */
    private void showRegisters(int upTo) {
        Map<String, String> latest = new LinkedHashMap<>();
        for (int i = 0; i <= upTo && i < steps.size(); i++) latest.putAll(StepRegisters.parse(steps.get(i)));
        for (Map.Entry<String, String> e : latest.entrySet()) {
            BitCells c = cells.get(e.getKey());
            if (c == null) {
                c = new BitCells(e.getValue().length(), true);
                cells.put(e.getKey(), c);
                Label name = Kit.plain(e.getKey(), "reg-name");
                HBox line = new HBox(12, name, c);
                line.setAlignment(Pos.CENTER_LEFT);
                registers.getChildren().add(line);
                c.set(e.getValue(), false);
            } else {
                c.set(e.getValue(), true);
            }
        }
        if (upTo < 0) for (BitCells c : cells.values()) c.set("0".repeat(c.get().length()), false);
    }

    @Override
    public Node diagram() {
        List<String> how = switch (mode) {
            case "booth" -> List.of(
                "Registers: A = 0, Q = multiplier, M = multiplicand, Q-1 = 0.",
                "Repeat n times: look at the last bit of Q and Q-1.",
                "Q0 Q-1 = 01 → A = A + M.   Q0 Q-1 = 10 → A = A - M.   00 or 11 → do nothing.",
                "Arithmetic shift right the combined A, Q, Q-1 (keep the sign bit).",
                "The product is in A followed by Q (2n bits).");
            case "restoring" -> List.of(
                "Registers: A = 0, Q = dividend, M = divisor.",
                "Repeat n times: shift A and Q left by one bit.",
                "A = A - M.",
                "If A is negative → restore (A = A + M) and set the quotient bit Q0 = 0.",
                "If A is not negative → keep it and set Q0 = 1.   At the end Q = quotient, A = remainder.");
            default -> List.of(
                "Registers: A = 0, Q = dividend, M = divisor.",
                "Repeat n times: shift A and Q left by one bit.",
                "If A was non-negative before the shift: A = A - M, otherwise A = A + M.",
                "If A is now non-negative set Q0 = 1, else Q0 = 0.",
                "After n steps, if A is negative add M once more. Q = quotient, A = remainder.");
        };
        return Kit.bullets(how);
    }

    @Override
    public Node interactive() {
        Label l1 = Kit.plain(mode.equals("booth") ? "Multiplicand" : "Dividend", "lab-title");
        Label l2 = Kit.plain(mode.equals("booth") ? "Multiplier" : "Divisor", "lab-title");
        HBox bar = new HBox(10, l1, first, l2, second, width, Kit.button("Run", "btn-primary", e -> run()));
        bar.setAlignment(Pos.CENTER_LEFT);
        return new VBox(14, Kit.label("Choose numbers and a register width, press Run, then play the steps. Changed bits flash yellow.", "muted"), bar, status, answer, bottom);
    }

    @Override
    public void dispose() {
        if (player != null) player.stop();
    }
}
