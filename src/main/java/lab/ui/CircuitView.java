package lab.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import playground.Circuit;
import playground.ComponentType;
import playground.Connection;
import playground.PlaygroundComponent;
import playground.SimulationResult;

/**
 * Draws a {@link Circuit} with proper gate symbols and animates the signals: when an input changes
 * the new values travel through the circuit level by level, wires light up green for 1, and
 * small dots flow along every wire that carries a 1.
 *
 * In a lesson the student can click the input switches; {@link EditableCircuitView} adds
 * drag-and-drop editing for the playground.
 */
class CircuitView extends Canvas {

    static final double STEP_SECONDS = 0.45;   // time for a change to cross one gate level
    static final double BUBBLE = 10;

    protected Circuit circuit;
    protected final Map<String, Boolean> shown = new HashMap<>();
    private Map<String, Boolean> from = new HashMap<>();
    private Map<String, Boolean> to = new HashMap<>();
    private Map<String, Integer> level = new HashMap<>();
    private int maxLevel = 0;
    private double animTime = 0;
    private boolean animating = false;
    private double phase = 0;
    private long last = 0;
    protected List<String> problems = List.of();
    protected Runnable onChange = () -> { };
    private final AnimationTimer timer;

    CircuitView(Circuit circuit, boolean clickableInputs) {
        super(560, 300);
        this.circuit = circuit;
        fitToCircuit();
        timer = new AnimationTimer() {
            @Override public void handle(long now) { tick(now); }
        };
        timer.start();
        if (clickableInputs) {
            setOnMouseClicked(e -> {
                String id = componentAt(e.getX(), e.getY());
                if (id != null && circuit().getComponent(id).getType() == ComponentType.INPUT) {
                    PlaygroundComponent c = circuit().getComponent(id);
                    circuit().setInputValue(id, !c.getInputValue());
                    changed(true);
                }
            });
        }
        refresh(false);
    }

    Circuit circuit() { return circuit; }

    void setOnChange(Runnable r) { this.onChange = r; }

    void stop() { timer.stop(); }

    void start() { timer.start(); }

    List<String> getProblems() { return problems; }

    /** Sizes the canvas so the whole circuit fits. */
    final void fitToCircuit() {
        double w = 560, h = 260;
        for (PlaygroundComponent c : circuit.getComponents()) {
            w = Math.max(w, c.getPosition().x() + 150);
            h = Math.max(h, c.getPosition().y() + 100);
        }
        setWidth(w);
        setHeight(h);
    }

    // ------------------------------------------------------------------ geometry

    static double widthOf(ComponentType t) {
        return switch (t) {
            case INPUT -> 64;
            case OUTPUT -> 88;
            case NOT -> 70;
            default -> 84;
        };
    }

    static double heightOf(ComponentType t) {
        return switch (t) {
            case INPUT, OUTPUT -> 36;
            case NOT -> 46;
            default -> 60;
        };
    }

    static boolean inverting(ComponentType t) {
        return t == ComponentType.NOT || t == ComponentType.NAND || t == ComponentType.NOR || t == ComponentType.XNOR;
    }

    static double[] inPort(PlaygroundComponent c, int port) {
        ComponentType t = c.getType();
        int n = Math.max(1, t.getInputPortCount());
        double x = c.getPosition().x();
        double y = c.getPosition().y();
        double h = heightOf(t);
        double py = n == 1 ? y + h / 2 : y + h * (port + 1) / (n + 1);
        return new double[] {x, py};
    }

    static double[] outPort(PlaygroundComponent c) {
        ComponentType t = c.getType();
        return new double[] {c.getPosition().x() + widthOf(t), c.getPosition().y() + heightOf(t) / 2};
    }

    /** Id of the component under the point, or null. */
    String componentAt(double mx, double my) {
        List<PlaygroundComponent> all = circuit.getComponents();
        for (int i = all.size() - 1; i >= 0; i--) {
            PlaygroundComponent c = all.get(i);
            double x = c.getPosition().x(), y = c.getPosition().y();
            if (mx >= x && mx <= x + widthOf(c.getType()) && my >= y && my <= y + heightOf(c.getType())) return c.getId();
        }
        return null;
    }

    // ------------------------------------------------------------------ simulation + animation

    /** Re-evaluates the circuit. With animate=true the new values travel through the gates. */
    final void refresh(boolean animate) {
        problems = circuit.validate();
        Map<String, Boolean> target = new HashMap<>();
        level = new HashMap<>();
        maxLevel = 0;
        if (problems.isEmpty()) {
            SimulationResult r = circuit.run();
            for (var s : r.steps()) target.put(s.componentId(), s.outputValue());
            for (var s : r.steps()) {
                int lv = 0;
                for (Connection cn : circuit.getConnections()) {
                    if (cn.targetId().equals(s.componentId())) lv = Math.max(lv, level.getOrDefault(cn.sourceId(), 0) + 1);
                }
                level.put(s.componentId(), lv);
                maxLevel = Math.max(maxLevel, lv);
            }
        }
        if (!animate || shown.isEmpty() || target.isEmpty()) {
            shown.clear();
            shown.putAll(target);
            animating = false;
        } else {
            from = new HashMap<>(shown);
            to = target;
            animTime = 0;
            animating = true;
        }
        draw();
    }

    /** Called after any change made by the user. */
    protected void changed(boolean animate) {
        refresh(animate);
        onChange.run();
    }

    private void tick(long now) {
        double dt = last == 0 ? 0 : (now - last) / 1_000_000_000.0;
        last = now;
        if (dt > 0.1) dt = 0.1;
        phase += dt * 70;
        if (animating) {
            animTime += dt;
            for (String id : to.keySet()) {
                int lv = level.getOrDefault(id, 0);
                boolean arrived = animTime >= lv * STEP_SECONDS;
                shown.put(id, arrived ? to.get(id) : from.getOrDefault(id, false));
            }
            if (animTime > (maxLevel + 1) * STEP_SECONDS) animating = false;
        }
        draw();
    }

    protected boolean value(String id) {
        return shown.getOrDefault(id, false);
    }

    // ------------------------------------------------------------------ drawing

    protected void drawBackground(GraphicsContext gc) {
        gc.setFill(Theme.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    protected void drawOverlay(GraphicsContext gc) { }

    protected void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        drawBackground(gc);
        for (Connection cn : circuit.getConnections()) drawWire(gc, cn);
        for (PlaygroundComponent c : circuit.getComponents()) drawComponent(gc, c);
        if (!problems.isEmpty() && !circuit.getComponents().isEmpty()) {
            gc.setFill(Theme.AMBER);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("⚠ " + problems.get(0), 14, getHeight() - 16);
        }
        drawOverlay(gc);
    }

    private static double bez(double p0, double p1, double p2, double p3, double t) {
        double u = 1 - t;
        return u * u * u * p0 + 3 * u * u * t * p1 + 3 * u * t * t * p2 + t * t * t * p3;
    }

    private void drawWire(GraphicsContext gc, Connection cn) {
        PlaygroundComponent src = circuit.getComponent(cn.sourceId());
        PlaygroundComponent dst = circuit.getComponent(cn.targetId());
        double[] a = outPort(src);
        double[] b = inPort(dst, cn.targetPort());
        double dx = Math.max(30, Math.abs(b[0] - a[0]) / 2);
        double c1x = a[0] + dx, c2x = b[0] - dx;
        boolean on = value(cn.sourceId());
        boolean fresh = animating && from.getOrDefault(cn.sourceId(), false) != to.getOrDefault(cn.sourceId(), false);
        gc.setStroke(on ? Theme.GREEN : Theme.WIRE_OFF);
        gc.setLineWidth(on ? 3.2 : 2.2);
        gc.beginPath();
        gc.moveTo(a[0], a[1]);
        gc.bezierCurveTo(c1x, a[1], c2x, b[1], b[0], b[1]);
        gc.stroke();
        if (on) {   // flowing dots show the direction of the signal
            gc.setFill(Color.web("#bbf7d0"));
            double len = Math.abs(b[0] - a[0]) + Math.abs(b[1] - a[1]) + 40;
            for (int i = 0; i < 4; i++) {
                double t = ((phase / len) + i / 4.0) % 1.0;
                gc.fillOval(bez(a[0], c1x, c2x, b[0], t) - 2.5, bez(a[1], a[1], b[1], b[1], t) - 2.5, 5, 5);
            }
        }
        if (fresh) {   // bright pulse while the change is travelling
            int lv = level.getOrDefault(cn.sourceId(), 0);
            double p = (animTime - lv * STEP_SECONDS) / STEP_SECONDS;
            if (p >= 0 && p <= 1) {
                gc.setFill(Theme.AMBER);
                gc.fillOval(bez(a[0], c1x, c2x, b[0], p) - 6, bez(a[1], a[1], b[1], b[1], p) - 6, 12, 12);
            }
        }
    }

    private void drawComponent(GraphicsContext gc, PlaygroundComponent c) {
        ComponentType t = c.getType();
        double x = c.getPosition().x(), y = c.getPosition().y();
        double w = widthOf(t), h = heightOf(t);
        boolean out = value(c.getId());
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        gc.setTextBaseline(VPos.CENTER);
        if (t == ComponentType.INPUT) {
            gc.setFill(out ? Theme.GREEN : Color.web("#e2e8f0"));
            gc.fillRoundRect(x, y, w, h, 12, 12);
            gc.setStroke(out ? Color.web("#15803d") : Theme.BORDER);
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x, y, w, h, 12, 12);
            gc.setFill(out ? Theme.WHITE : Theme.TEXT);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(c.getLabel() + " = " + (c.getInputValue() ? "1" : "0"), x + w / 2, y + h / 2);
            drawPort(gc, x + w, y + h / 2);
            return;
        }
        if (t == ComponentType.OUTPUT) {
            double cx = x + 18, cy = y + h / 2;
            if (out) {   // glow
                gc.setFill(Color.rgb(250, 204, 21, 0.28));
                gc.fillOval(cx - 26, cy - 26, 52, 52);
            }
            gc.setFill(out ? Color.web("#facc15") : Color.web("#cbd5e1"));
            gc.fillOval(cx - 15, cy - 15, 30, 30);
            gc.setStroke(out ? Color.web("#ca8a04") : Theme.WIRE_OFF);
            gc.setLineWidth(2);
            gc.strokeOval(cx - 15, cy - 15, 30, 30);
            gc.setFill(Theme.TEXT);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(c.getLabel() + " = " + (out ? "1" : "0"), x + 40, cy);
            drawPort(gc, x, cy);
            return;
        }
        drawGate(gc, t, x, y, w, h, out);
        for (int p = 0; p < t.getInputPortCount(); p++) {
            double[] ip = inPort(c, p);
            drawPort(gc, ip[0], ip[1]);
        }
        double[] op = outPort(c);
        drawPort(gc, op[0], op[1]);
        if (!(this instanceof EditableCircuitView)) return;
        gc.setFill(Theme.MUTED);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        gc.fillText(c.getLabel(), x + w / 2, y + h + 10);
    }

    protected void drawPort(GraphicsContext gc, double px, double py) {
        gc.setFill(Theme.WHITE);
        gc.fillOval(px - 4, py - 4, 8, 8);
        gc.setStroke(Theme.GATE_LINE);
        gc.setLineWidth(1.5);
        gc.strokeOval(px - 4, py - 4, 8, 8);
    }

    static void drawGate(GraphicsContext gc, ComponentType t, double x, double y, double w, double h, boolean out) {
        boolean inv = inverting(t);
        double bw = inv ? w - BUBBLE : w;
        gc.setFill(out ? Theme.GREEN_LIGHT : Theme.GATE_FILL);
        gc.setStroke(out ? Color.web("#15803d") : Theme.GATE_LINE);
        gc.setLineWidth(2.2);
        gc.beginPath();
        switch (t) {
            case AND, NAND -> {
                gc.moveTo(x, y);
                gc.lineTo(x + bw / 2, y);
                for (int i = 0; i <= 18; i++) {
                    double a = Math.toRadians(-90 + 180.0 * i / 18);
                    gc.lineTo(x + bw / 2 + (bw / 2) * Math.cos(a), y + h / 2 + (h / 2) * Math.sin(a));
                }
                gc.lineTo(x, y + h);
            }
            case OR, NOR, XOR, XNOR -> {
                gc.moveTo(x, y);
                gc.quadraticCurveTo(x + bw * 0.6, y, x + bw, y + h / 2);
                gc.quadraticCurveTo(x + bw * 0.6, y + h, x, y + h);
                gc.quadraticCurveTo(x + bw * 0.3, y + h / 2, x, y);
            }
            default -> {   // NOT
                gc.moveTo(x, y);
                gc.lineTo(x + bw, y + h / 2);
                gc.lineTo(x, y + h);
            }
        }
        gc.closePath();
        gc.fill();
        gc.stroke();
        if (t == ComponentType.XOR || t == ComponentType.XNOR) {
            gc.beginPath();
            gc.moveTo(x - 8, y);
            gc.quadraticCurveTo(x - 8 + bw * 0.3, y + h / 2, x - 8, y + h);
            gc.stroke();
        }
        if (inv) {
            gc.setFill(Theme.WHITE);
            gc.fillOval(x + w - BUBBLE, y + h / 2 - BUBBLE / 2, BUBBLE, BUBBLE);
            gc.strokeOval(x + w - BUBBLE, y + h / 2 - BUBBLE / 2, BUBBLE, BUBBLE);
        }
        if (t != ComponentType.NOT) {
            gc.setFill(Theme.GATE_LINE);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(t.name(), x + bw / 2 + (t == ComponentType.AND || t == ComponentType.NAND ? -4 : 0), y + h / 2);
        }
    }
}
