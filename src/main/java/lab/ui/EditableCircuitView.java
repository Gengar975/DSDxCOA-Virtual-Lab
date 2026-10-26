package lab.ui;

import java.util.function.Consumer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import playground.Circuit;
import playground.ComponentType;
import playground.Connection;
import playground.PlaygroundComponent;

/**
 * The playground canvas. Students can drop components on it, drag them around, draw wires from an
 * output dot to an input dot, click INPUT switches and delete parts. All rules (no loops, one wire
 * per input) come from the {@link Circuit} engine; its messages are passed on to the status bar.
 */
final class EditableCircuitView extends CircuitView {

    private String selected;
    private String dragId;
    private double dragDX, dragDY, pressX, pressY;
    private boolean moved;
    private String wireFrom;
    private double mouseX, mouseY;
    private Consumer<String> messages = s -> { };

    EditableCircuitView(Circuit circuit) {
        super(circuit, false);
        setWidth(1000);
        setHeight(560);
        setFocusTraversable(true);
        setOnMousePressed(this::pressed);
        setOnMouseDragged(this::dragged);
        setOnMouseReleased(this::released);
        refresh(false);
    }

    void setMessages(Consumer<String> m) { this.messages = m; }

    String getSelected() { return selected; }

    /** Replaces the whole circuit (for the ready-made examples). */
    void setCircuit(Circuit c) {
        this.circuit = c;
        selected = null;
        wireFrom = null;
        shown.clear();
        changed(false);
    }

    void addComponent(ComponentType type, double x, double y) {
        double px = Math.max(10, Math.min(x - widthOf(type) / 2, getWidth() - widthOf(type) - 10));
        double py = Math.max(10, Math.min(y - heightOf(type) / 2, getHeight() - heightOf(type) - 10));
        selected = circuit.addComponent(type, px, py);
        if (type == ComponentType.INPUT || type == ComponentType.OUTPUT) {
            String prefix = type == ComponentType.INPUT ? "In" : "Out";
            int n = 0;
            for (PlaygroundComponent c : circuit.getComponents()) if (c.getType() == type) n++;
            circuit.setLabel(selected, type == ComponentType.INPUT ? String.valueOf((char) ('A' + Math.min(25, n - 1))) : prefix + n);
        }
        changed(false);
    }

    void deleteSelected() {
        if (selected == null) { messages.accept("Click a component first, then press Delete."); return; }
        circuit.removeComponent(selected);
        selected = null;
        changed(false);
    }

    void clearAll() {
        circuit.clear();
        selected = null;
        shown.clear();
        changed(false);
    }

    void resetInputs() {
        circuit.reset();
        changed(true);
    }

    @Override
    protected void drawBackground(GraphicsContext gc) {
        super.drawBackground(gc);
        gc.setFill(Color.web("#cbd5e1"));
        for (double gx = 20; gx < getWidth(); gx += 24)
            for (double gy = 20; gy < getHeight(); gy += 24) gc.fillRect(gx, gy, 1.6, 1.6);
        if (circuit.getComponents().isEmpty()) {
            gc.setFill(Theme.MUTED);
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.setTextBaseline(javafx.geometry.VPos.CENTER);
            gc.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.NORMAL, 17));
            gc.fillText("Drag a component from the left panel and drop it here", getWidth() / 2, getHeight() / 2 - 12);
            gc.fillText("or load a ready-made circuit from the toolbar", getWidth() / 2, getHeight() / 2 + 14);
        }
    }

    @Override
    protected void drawOverlay(GraphicsContext gc) {
        if (selected != null) {
            try {
                PlaygroundComponent c = circuit.getComponent(selected);
                gc.setStroke(Theme.INDIGO);
                gc.setLineWidth(2);
                gc.setLineDashes(6, 4);
                gc.strokeRoundRect(c.getPosition().x() - 6, c.getPosition().y() - 6,
                        widthOf(c.getType()) + 12, heightOf(c.getType()) + 12, 10, 10);
                gc.setLineDashes();
            } catch (IllegalArgumentException gone) {
                selected = null;
            }
        }
        if (wireFrom != null) {
            double[] a = outPort(circuit.getComponent(wireFrom));
            gc.setStroke(Theme.INDIGO);
            gc.setLineWidth(2.5);
            gc.setLineDashes(5, 4);
            gc.strokeLine(a[0], a[1], mouseX, mouseY);
            gc.setLineDashes();
        }
    }

    private String outputPortAt(double mx, double my) {
        for (PlaygroundComponent c : circuit.getComponents()) {
            if (!c.getType().hasOutputPort()) continue;
            double[] p = outPort(c);
            if (Math.hypot(mx - p[0], my - p[1]) <= 9) return c.getId();
        }
        return null;
    }

    /** Returns {componentId, port} of the input dot under the mouse, or null. */
    private Object[] inputPortAt(double mx, double my) {
        for (PlaygroundComponent c : circuit.getComponents()) {
            for (int p = 0; p < c.getType().getInputPortCount(); p++) {
                double[] pt = inPort(c, p);
                if (Math.hypot(mx - pt[0], my - pt[1]) <= 10) return new Object[] {c.getId(), p};
            }
        }
        return null;
    }

    private void pressed(MouseEvent e) {
        requestFocus();
        pressX = e.getX();
        pressY = e.getY();
        mouseX = pressX;
        mouseY = pressY;
        moved = false;
        String outId = outputPortAt(pressX, pressY);
        if (outId != null) {
            wireFrom = outId;
            return;
        }
        Object[] in = inputPortAt(pressX, pressY);
        if (in != null) {
            for (Connection cn : circuit.getConnections()) {
                if (cn.targetId().equals(in[0]) && cn.targetPort() == (Integer) in[1]) {
                    circuit.disconnect((String) in[0], (Integer) in[1]);
                    messages.accept("Wire removed. Drag from an output dot to an input dot to wire again.");
                    changed(false);
                    return;
                }
            }
        }
        String id = componentAt(pressX, pressY);
        selected = id;
        if (id != null) {
            PlaygroundComponent c = circuit.getComponent(id);
            dragId = id;
            dragDX = pressX - c.getPosition().x();
            dragDY = pressY - c.getPosition().y();
        }
        draw();
    }

    private void dragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (Math.hypot(mouseX - pressX, mouseY - pressY) > 3) moved = true;
        if (dragId != null && moved) {
            ComponentType t = circuit.getComponent(dragId).getType();
            double nx = Math.max(4, Math.min(mouseX - dragDX, getWidth() - widthOf(t) - 4));
            double ny = Math.max(4, Math.min(mouseY - dragDY, getHeight() - heightOf(t) - 4));
            circuit.moveComponent(dragId, nx, ny);
        }
        draw();
    }

    private void released(MouseEvent e) {
        if (wireFrom != null) {
            Object[] in = inputPortAt(e.getX(), e.getY());
            if (in != null) {
                try {
                    circuit.connect(wireFrom, (String) in[0], (Integer) in[1]);
                    messages.accept("Connected " + circuit.getComponent(wireFrom).getLabel() + " to " + circuit.getComponent((String) in[0]).getLabel() + ".");
                } catch (IllegalArgumentException ex) {
                    messages.accept("Cannot connect: " + ex.getMessage());
                }
            } else if (moved) {
                messages.accept("Drop the wire on an input dot of a gate or output.");
            }
            wireFrom = null;
            changed(true);
        } else if (dragId != null) {
            PlaygroundComponent c = circuit.getComponent(dragId);
            if (!moved && c.getType() == ComponentType.INPUT) {
                circuit.setInputValue(dragId, !c.getInputValue());
                changed(true);
            } else {
                changed(false);
            }
        }
        dragId = null;
    }
}
