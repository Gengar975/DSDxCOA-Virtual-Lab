package lab.ui;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import playground.ComponentType;

/** A large, labelled drawing of one gate symbol with its Boolean expression. */
final class GateSymbolView extends Canvas {

    GateSymbolView(ComponentType type, String expression, String note) {
        super(560, 200);
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Theme.PANEL);
        gc.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        double scale = 1.7;
        double gw = CircuitView.widthOf(type), gh = CircuitView.heightOf(type);
        double gx = 90, gy = 100 - gh * scale / 2;
        gc.save();
        gc.translate(gx, gy);
        gc.scale(scale, scale);
        CircuitView.drawGate(gc, type, 0, 0, gw, gh, false);
        gc.restore();
        gc.setStroke(Theme.WIRE_OFF);
        gc.setLineWidth(3);
        int n = type.getInputPortCount();
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        gc.setFill(Theme.TEXT);
        gc.setTextBaseline(VPos.CENTER);
        for (int i = 0; i < n; i++) {
            double py = n == 1 ? gy + gh * scale / 2 : gy + gh * scale * (i + 1) / (n + 1);
            gc.strokeLine(gx - 50, py, gx, py);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(n == 1 ? "A" : String.valueOf((char) ('A' + i)), gx - 58, py);
        }
        double ox = gx + gw * scale;
        gc.strokeLine(ox, 100, ox + 50, 100);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Y", ox + 58, 100);
        gc.setFill(Theme.INDIGO);
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 26));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(expression, 330, 78);
        gc.setFill(Theme.MUTED);
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        gc.fillText(note, 330, 118);
        gc.setFill(Color.TRANSPARENT);
    }
}
