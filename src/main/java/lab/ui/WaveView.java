package lab.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Timing diagram: one row per signal, one column per clock cycle. The newest column draws itself
 * from left to right so students can see the waveform being produced.
 */
final class WaveView extends Canvas {

    private static final int MAX = 14;
    private static final double COL = 52, ROW = 44, LEFT = 70;

    private final List<String> names;
    private final List<boolean[]> samples = new ArrayList<>();
    private double progress = 1;
    private final Timeline grow = new Timeline(new KeyFrame(Duration.millis(16), e -> {
        progress = Math.min(1, progress + 0.07);
        draw();
    }));

    WaveView(List<String> names) {
        super(LEFT + MAX * COL + 20, names.size() * ROW + 16);
        this.names = names;
        grow.setCycleCount(15);
        draw();
    }

    void clear() {
        samples.clear();
        draw();
    }

    /** Adds one clock cycle; values are in the same order as the names. */
    void push(boolean... values) {
        samples.add(values.clone());
        while (samples.size() > MAX) samples.remove(0);
        progress = 0;
        grow.stop();
        grow.play();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Theme.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        gc.setTextBaseline(VPos.CENTER);
        for (int r = 0; r < names.size(); r++) {
            double yBase = 8 + r * ROW + ROW - 8, yTop = 8 + r * ROW + 8;
            gc.setFill(Theme.TEXT);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(names.get(r), 8, (yBase + yTop) / 2);
            gc.setStroke(Theme.BORDER);
            gc.setLineWidth(1);
            gc.strokeLine(LEFT, yBase, getWidth() - 10, yBase);
            gc.setStroke(r == 0 ? Theme.INDIGO : Theme.GREEN);
            gc.setLineWidth(2.6);
            for (int c = 0; c < samples.size(); c++) {
                double x0 = LEFT + c * COL;
                double span = (c == samples.size() - 1) ? COL * progress : COL;
                boolean v = samples.get(c)[r];
                double y = v ? yTop : yBase;
                double prevY = c == 0 ? y : (samples.get(c - 1)[r] ? yTop : yBase);
                if (prevY != y) gc.strokeLine(x0, prevY, x0, y);
                if (r == 0) {   // clock: high for the first half of the cycle, then low
                    double half = Math.min(span, COL / 2);
                    gc.strokeLine(x0, yTop, x0 + half, yTop);
                    if (span > COL / 2) {
                        gc.strokeLine(x0 + COL / 2, yTop, x0 + COL / 2, yBase);
                        gc.strokeLine(x0 + COL / 2, yBase, x0 + span, yBase);
                    }
                } else {
                    gc.strokeLine(x0, y, x0 + span, y);
                }
            }
        }
        if (samples.isEmpty()) {
            gc.setFill(Theme.MUTED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("press Clock pulse to draw the timing diagram", getWidth() / 2 + 30, getHeight() / 2);
        }
    }
}
