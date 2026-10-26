package lab.ui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * A "chip" drawing: input switches on the left, the circuit as a labelled block, output lamps on the right.
 * The logic comes from the project's own circuit classes (passed in as a function). When an input is
 * clicked a pulse travels along the wires into the chip and out to the outputs, then the lamps update.
 */
final class BlockView extends Canvas {

    private static final double ROW = 34;

    private final String title;
    private final List<String> ins;
    private final List<String> outs;
    private final Function<Map<String, Boolean>, Map<String, Boolean>> logic;
    private final Function<Map<String, Boolean>, Set<String>> active;
    private final boolean interactive;
    private final Map<String, Boolean> inVals = new LinkedHashMap<>();
    private Map<String, Boolean> outShown = new HashMap<>();
    private Map<String, Boolean> outTarget = new HashMap<>();
    private Set<String> activeIns = Set.of();
    private String error = null;
    private double anim = 99;   // seconds since the last change
    private double phase;
    private long last;
    private Runnable onChange = () -> { };
    private final AnimationTimer timer;

    BlockView(String title, List<String> ins, List<String> outs, boolean interactive,
              Function<Map<String, Boolean>, Map<String, Boolean>> logic,
              Function<Map<String, Boolean>, Set<String>> active) {
        super(640, Math.max(ins.size(), outs.size()) * ROW + 56);
        this.title = title;
        this.ins = ins;
        this.outs = outs;
        this.logic = logic;
        this.active = active;
        this.interactive = interactive;
        for (String s : ins) inVals.put(s, false);
        if (interactive) {
            setOnMouseClicked(e -> {
                for (int i = 0; i < ins.size(); i++) {
                    double y = 28 + i * ROW + (rows(ins.size()) - ins.size()) * ROW / 2;
                    if (e.getX() > 20 && e.getX() < 110 && Math.abs(e.getY() - y) < 15) {
                        inVals.put(ins.get(i), !inVals.get(ins.get(i)));
                        evaluate(true);
                        onChange.run();
                    }
                }
            });
        }
        evaluate(false);
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                double dt = last == 0 ? 0 : Math.min(0.1, (now - last) / 1e9);
                last = now;
                phase += dt * 70;
                anim += dt;
                if (anim >= 0.55) outShown = outTarget;
                draw();
            }
        };
        timer.start();
    }

    private static int rows(int n) { return n; }

    void stop() { timer.stop(); }

    void setOnChange(Runnable r) { this.onChange = r; }

    Map<String, Boolean> inputs() { return new LinkedHashMap<>(inVals); }

    String getError() { return error; }

    void setInput(String name, boolean v) {
        inVals.put(name, v);
        evaluate(true);
    }

    private void evaluate(boolean animate) {
        try {
            if (logic != null) outTarget = logic.apply(new LinkedHashMap<>(inVals));
            activeIns = active == null ? Set.of() : active.apply(new LinkedHashMap<>(inVals));
            error = null;
        } catch (IllegalArgumentException ex) {
            error = ex.getMessage();
            outTarget = new HashMap<>();
        }
        if (animate) anim = 0; else { outShown = outTarget; anim = 99; }
    }

    private double inY(int i) { return 28 + i * ROW + (Math.max(ins.size(), outs.size()) - ins.size()) * ROW / 2; }

    private double outY(int i) { return 28 + i * ROW + (Math.max(ins.size(), outs.size()) - outs.size()) * ROW / 2; }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double h = getHeight();
        gc.setFill(Theme.WHITE);
        gc.fillRect(0, 0, getWidth(), h);
        double chipX = 230, chipW = 180;
        // input wires + switches
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        gc.setTextBaseline(VPos.CENTER);
        for (int i = 0; i < ins.size(); i++) {
            String n = ins.get(i);
            double y = inY(i);
            boolean on = inVals.get(n);
            boolean path = activeIns.contains(n);
            gc.setStroke(on ? Theme.GREEN : Theme.WIRE_OFF);
            gc.setLineWidth(path ? 4.5 : on ? 3.2 : 2.2);
            if (path) gc.setStroke(Theme.AMBER);
            gc.strokeLine(110, y, chipX, y);
            if (on && interactive) {
                gc.setFill(Color.web("#bbf7d0"));
                for (double d = phase % 36; d < chipX - 110; d += 36) gc.fillOval(110 + d - 2.5, y - 2.5, 5, 5);
            }
            if (anim < 0.55 && on) {
                gc.setFill(Theme.AMBER);
                double p = Math.min(1, anim / 0.55);
                gc.fillOval(110 + p * (chipX - 110) - 6, y - 6, 12, 12);
            }
            if (interactive) {
                gc.setFill(on ? Theme.GREEN : Color.web("#e2e8f0"));
                gc.fillRoundRect(20, y - 14, 90, 28, 10, 10);
                gc.setFill(on ? Theme.WHITE : Theme.TEXT);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(n + " = " + (on ? 1 : 0), 65, y);
            } else {
                gc.setFill(Theme.TEXT);
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText(n, 100, y);
            }
        }
        // chip
        gc.setFill(Theme.GATE_FILL);
        gc.fillRoundRect(chipX, 10, chipW, h - 20, 16, 16);
        gc.setStroke(Theme.GATE_LINE);
        gc.setLineWidth(2.5);
        gc.strokeRoundRect(chipX, 10, chipW, h - 20, 16, 16);
        gc.setFill(Theme.GATE_LINE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(title, chipX + chipW / 2, h / 2);
        // outputs
        for (int i = 0; i < outs.size(); i++) {
            String n = outs.get(i);
            double y = outY(i);
            boolean on = outShown.getOrDefault(n, false) && interactive;
            gc.setStroke(on ? Theme.GREEN : Theme.WIRE_OFF);
            gc.setLineWidth(on ? 3.4 : 2.2);
            gc.strokeLine(chipX + chipW, y, 520, y);
            if (on) {
                gc.setFill(Color.web("#bbf7d0"));
                for (double d = phase % 36; d < 520 - chipX - chipW; d += 36) gc.fillOval(chipX + chipW + d - 2.5, y - 2.5, 5, 5);
            }
            if (anim >= 0.55 && anim < 1.1 && on) {
                gc.setFill(Theme.AMBER);
                double p = (anim - 0.55) / 0.55;
                gc.fillOval(chipX + chipW + p * (520 - chipX - chipW) - 6, y - 6, 12, 12);
            }
            if (interactive) {
                if (on) {
                    gc.setFill(Color.rgb(250, 204, 21, 0.3));
                    gc.fillOval(540 - 20, y - 20, 40, 40);
                }
                gc.setFill(on ? Color.web("#facc15") : Color.web("#cbd5e1"));
                gc.fillOval(540 - 12, y - 12, 24, 24);
                gc.setStroke(on ? Color.web("#ca8a04") : Theme.WIRE_OFF);
                gc.setLineWidth(2);
                gc.strokeOval(540 - 12, y - 12, 24, 24);
            }
            gc.setFill(Theme.TEXT);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(interactive ? n + " = " + (on ? 1 : 0) : n, interactive ? 566 : 530, y);
        }
        if (error != null) {
            gc.setFill(Theme.RED);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            gc.fillText("⚠ " + error, 20, h - 8);
        }
    }
}
