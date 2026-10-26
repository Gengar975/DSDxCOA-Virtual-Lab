package lab.ui;

import dsd.booleanlogic.KMapSolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lab.model.KMapAnalyzer;

/** Interactive Karnaugh map: click cells to set 1s; groups are circled and the expression is simplified. */
final class KMapLab implements Lab {

    private static final double CELL_W = 96, CELL_H = 72, LEFT = 90, TOP = 56;
    private static final String[] GRAY = {"00", "01", "11", "10"};

    private int n = 3;
    private final Set<Integer> ones = new TreeSet<>();
    private final Canvas canvas = new Canvas(520, 360);
    private final Label result = Kit.label("", "formula");
    private final FlowPane legend = new FlowPane(14, 6);
    private double reveal = 1;
    private List<KMapAnalyzer.Group> groups = new ArrayList<>();
    private final Timeline fade = new Timeline(new KeyFrame(Duration.millis(16), e -> {
        reveal = Math.min(1, reveal + 0.06);
        draw();
    }));

    KMapLab() {
        fade.setCycleCount(18);
        canvas.setOnMouseClicked(e -> {
            int c = (int) Math.floor((e.getX() - LEFT) / CELL_W);
            int r = (int) Math.floor((e.getY() - TOP) / CELL_H);
            if (r >= 0 && r < KMapAnalyzer.rows(n) && c >= 0 && c < KMapAnalyzer.cols(n)) {
                int m = KMapAnalyzer.mintermAt(n, r, c);
                if (!ones.remove(m)) ones.add(m);
                update();
            }
        });
        ones.addAll(List.of(1, 3));
        update();
    }

    private void update() {
        groups = KMapAnalyzer.analyze(n, ones);
        legend.getChildren().clear();
        for (int i = 0; i < groups.size(); i++) {
            Label l = Kit.plain("■ " + groups.get(i).term() + "  (" + groups.get(i).minterms().size() + " cells)", "formula");
            Color c = Theme.GROUPS[i % Theme.GROUPS.length];
            l.setStyle("-fx-text-fill: " + String.format("#%02x%02x%02x", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255)) + ";");
            legend.getChildren().add(l);
        }
        result.setText(ones.isEmpty() ? "F = 0   (click cells to place 1s)" : "F = " + KMapAnalyzer.expression(groups, ones)
                + "\nMinterms: " + ones);
        reveal = 0;
        fade.stop();
        fade.play();
        draw();
    }

    private String varNames(boolean rowsSide) {
        return switch (n) {
            case 2 -> rowsSide ? "A" : "B";
            case 3 -> rowsSide ? "A" : "BC";
            default -> rowsSide ? "AB" : "CD";
        };
    }

    private void draw() {
        int rows = KMapAnalyzer.rows(n), cols = KMapAnalyzer.cols(n);
        canvas.setWidth(LEFT + cols * CELL_W + 20);
        canvas.setHeight(TOP + rows * CELL_H + 20);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Theme.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        KMapSolver solver = new KMapSolver(n);   // the project's solver provides the grid values
        solver.populateMinterms(ones);
        int[][] grid = solver.getGrid();
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        gc.setFill(Theme.MUTED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(varNames(true) + " \\ " + varNames(false), LEFT / 2, TOP / 2);
        for (int c = 0; c < cols; c++) gc.fillText(n == 2 ? String.valueOf(c) : GRAY[c], LEFT + c * CELL_W + CELL_W / 2, TOP / 2);
        for (int r = 0; r < rows; r++) gc.fillText(n == 4 ? GRAY[r] : String.valueOf(r), LEFT / 2, TOP + r * CELL_H + CELL_H / 2);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = LEFT + c * CELL_W, y = TOP + r * CELL_H;
                boolean one = grid[r][c] == 1;
                gc.setFill(one ? Theme.GREEN_LIGHT : Theme.PANEL);
                gc.fillRect(x, y, CELL_W, CELL_H);
                gc.setStroke(Theme.BORDER);
                gc.setLineWidth(1.5);
                gc.strokeRect(x, y, CELL_W, CELL_H);
                gc.setFill(one ? Color.web("#166534") : Theme.WIRE_OFF);
                gc.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(grid[r][c]), x + CELL_W / 2, y + CELL_H / 2 + 2);
                gc.setFill(Theme.MUTED);
                gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("m" + KMapAnalyzer.mintermAt(n, r, c), x + 6, y + 11);
            }
        }
        for (int g = 0; g < groups.size(); g++) {
            Color col = Theme.GROUPS[g % Theme.GROUPS.length];
            double inset = 5 + g * 4;
            for (int m : groups.get(g).minterms()) {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (KMapAnalyzer.mintermAt(n, r, c) != m) continue;
                        double x = LEFT + c * CELL_W + inset, y = TOP + r * CELL_H + inset;
                        gc.setFill(Color.color(col.getRed(), col.getGreen(), col.getBlue(), 0.16 * reveal));
                        gc.fillRoundRect(x, y, CELL_W - 2 * inset, CELL_H - 2 * inset, 14, 14);
                        gc.setStroke(Color.color(col.getRed(), col.getGreen(), col.getBlue(), reveal));
                        gc.setLineWidth(3);
                        gc.strokeRoundRect(x, y, CELL_W - 2 * inset, CELL_H - 2 * inset, 14, 14);
                    }
                }
            }
        }
    }

    @Override
    public Node diagram() {
        String t = "Gray-code order: neighbouring labels differ in exactly ONE bit (00 → 01 → 11 → 10), and the last label (10) is also a neighbour of the first (00), so the map wraps around like a cylinder. "
                + "That is why cells next to each other, even across the edge, can be merged into one simpler term.";
        return Kit.label(t, "body-text");
    }

    @Override
    public Node interactive() {
        ComboBox<String> vars = Kit.combo("2 variables (A B)", "3 variables (A B C)", "4 variables (A B C D)");
        vars.setValue("3 variables (A B C)");
        vars.setOnAction(e -> {
            n = 2 + vars.getSelectionModel().getSelectedIndex();
            ones.removeIf(m -> m >= (1 << n));
            update();
        });
        HBox bar = new HBox(10, vars, Kit.button("Clear", "btn-secondary", e -> { ones.clear(); update(); }),
            Kit.button("Try an example", "btn-secondary", e -> {
                ones.clear();
                ones.addAll(n == 2 ? List.of(1, 3) : n == 3 ? List.of(0, 1, 5, 7) : List.of(0, 2, 8, 10, 5, 7));
                update();
            }));
        bar.setAlignment(Pos.CENTER_LEFT);
        return new VBox(12, Kit.label("Click a cell to flip it between 0 and 1. The coloured outlines are the groups; every group becomes one product term.", "muted"),
            bar, canvas, legend, result);
    }

    @Override
    public void dispose() { fade.stop(); }
}
