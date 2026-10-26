package lab.ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * "Think of it as switches and a lamp": AND = two switches in series, OR = two in parallel,
 * NOT = a switch that is closed while the input is 0. Click the switches to light the lamp.
 */
final class SwitchAnalogyView extends Canvas {

    private final String kind;   // "and", "or", "not"
    private boolean a;
    private boolean b;
    private double phase;
    private final AnimationTimer timer;

    SwitchAnalogyView(String kind) {
        super(560, 230);
        this.kind = kind;
        setOnMouseClicked(e -> {
            double x = e.getX(), y = e.getY();
            if (kind.equals("and")) {
                if (x > 130 && x < 220 && y > 90 && y < 150) a = !a;
                if (x > 250 && x < 340 && y > 90 && y < 150) b = !b;
            } else if (kind.equals("or")) {
                if (x > 180 && x < 290 && y > 40 && y < 100) a = !a;
                if (x > 180 && x < 290 && y > 130 && y < 190) b = !b;
            } else {
                if (x > 150 && x < 270 && y > 90 && y < 150) a = !a;
            }
            draw();
        });
        timer = new AnimationTimer() {
            @Override public void handle(long now) { phase += 1.4; draw(); }
        };
        timer.start();
        draw();
    }

    void stop() { timer.stop(); }

    private boolean lit() {
        return switch (kind) {
            case "and" -> a && b;
            case "or" -> a || b;
            default -> !a;
        };
    }

    private void wire(GraphicsContext gc, double x1, double y1, double x2, double y2, boolean on) {
        gc.setStroke(on ? Theme.GREEN : Theme.WIRE_OFF);
        gc.setLineWidth(on ? 4 : 3);
        gc.strokeLine(x1, y1, x2, y2);
        if (on) {
            gc.setFill(Color.web("#bbf7d0"));
            double len = Math.hypot(x2 - x1, y2 - y1);
            for (double d = (phase % 30); d < len; d += 30) {
                gc.fillOval(x1 + (x2 - x1) * d / len - 3, y1 + (y2 - y1) * d / len - 3, 6, 6);
            }
        }
    }

    /** Lever switch from (x,y) to (x+w,y); closed = flat, open = raised. */
    private void lever(GraphicsContext gc, double x, double y, double w, boolean closed, String name) {
        gc.setStroke(closed ? Theme.GREEN : Theme.RED);
        gc.setLineWidth(5);
        gc.strokeLine(x, y, closed ? x + w : x + w - 6, closed ? y : y - 34);
        gc.setFill(Theme.GATE_LINE);
        gc.fillOval(x - 5, y - 5, 10, 10);
        gc.fillOval(x + w - 5, y - 5, 10, 10);
        gc.setFill(Theme.TEXT);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(name, x + w / 2, y + 22);
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Theme.PANEL);
        gc.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        boolean on = lit();
        double lampX = 470, midY = 120;
        // battery
        gc.setFill(Theme.INK);
        gc.fillRect(36, midY - 26, 8, 52);
        gc.fillRect(52, midY - 14, 6, 28);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("battery", 47, midY + 44);
        if (kind.equals("and")) {
            wire(gc, 58, midY, 130, midY, on);
            lever(gc, 130, midY, 90, a, "A = " + (a ? 1 : 0));
            wire(gc, 220, midY, 250, midY, on);
            lever(gc, 250, midY, 90, b, "B = " + (b ? 1 : 0));
            wire(gc, 340, midY, lampX - 22, midY, on);
        } else if (kind.equals("or")) {
            wire(gc, 58, midY, 120, midY, on);
            wire(gc, 120, midY, 120, 70, a && on);
            wire(gc, 120, midY, 120, 160, b && on);
            wire(gc, 120, 70, 180, 70, a && on);
            lever(gc, 180, 70, 110, a, "A = " + (a ? 1 : 0));
            wire(gc, 290, 70, 350, 70, a && on);
            wire(gc, 120, 160, 180, 160, b && on);
            lever(gc, 180, 160, 110, b, "B = " + (b ? 1 : 0));
            wire(gc, 290, 160, 350, 160, b && on);
            wire(gc, 350, 70, 350, midY, a && on);
            wire(gc, 350, 160, 350, midY, b && on);
            wire(gc, 350, midY, lampX - 22, midY, on);
        } else {
            wire(gc, 58, midY, 150, midY, on);
            lever(gc, 150, midY, 120, !a, "contact (opens when A = 1)");
            wire(gc, 270, midY, lampX - 22, midY, on);
            gc.setFill(Theme.MUTED);
            gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
            gc.fillText("Click the contact to flip input A = " + (a ? 1 : 0), 210, 195);
        }
        // lamp
        if (on) {
            gc.setFill(Color.rgb(250, 204, 21, 0.30));
            gc.fillOval(lampX - 38, midY - 38, 76, 76);
        }
        gc.setFill(on ? Color.web("#facc15") : Color.web("#cbd5e1"));
        gc.fillOval(lampX - 22, midY - 22, 44, 44);
        gc.setStroke(on ? Color.web("#ca8a04") : Theme.WIRE_OFF);
        gc.setLineWidth(3);
        gc.strokeOval(lampX - 22, midY - 22, 44, 44);
        gc.setFill(Theme.TEXT);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        gc.fillText("Y = " + (on ? 1 : 0), lampX, midY + 46);
        gc.setFill(Theme.MUTED);
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        gc.fillText("Click a switch to open / close it", 280, 214);
    }
}
