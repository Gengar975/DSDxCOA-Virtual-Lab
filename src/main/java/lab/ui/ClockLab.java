package lab.ui;

import dsd.sequential.clock.Clock;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/** A running clock waveform with rising/falling edge markers and a pulse counter driven by the project's Clock class. */
final class ClockLab implements Lab {

    private final Canvas canvas = new Canvas(760, 190);
    private final Clock clock = new Clock();
    private final Slider speed = new Slider(0.5, 4, 1.5);
    private final Label info = Kit.plain("", "formula");
    private double time = 0;
    private boolean running = true;
    private boolean wasHigh = false;
    private long last = 0;
    private final AnimationTimer timer;

    ClockLab() {
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                double dt = last == 0 ? 0 : Math.min(0.1, (now - last) / 1e9);
                last = now;
                if (running) {
                    time += dt;
                    boolean high = (time * speed.getValue()) % 1.0 < 0.5;
                    if (high && !wasHigh) {
                        clock.pulse();   // rising edge = one clock pulse
                    }
                    wasHigh = high;
                }
                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double f = speed.getValue();
        gc.setFill(Theme.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double yHi = 50, yLo = 140, pxPerSec = 150, nowX = 560;
        gc.setStroke(Theme.INDIGO);
        gc.setLineWidth(3);
        double prevY = Double.NaN;
        for (double x = 20; x < 740; x += 2) {
            double t = time - (nowX - x) / pxPerSec;
            double y = (t * f) % 1.0 < 0 ? yLo : ((t * f) % 1.0) < 0.5 ? yHi : yLo;
            if (t < 0) y = yLo;
            if (!Double.isNaN(prevY) && prevY != y) {
                gc.strokeLine(x, prevY, x, y);
                gc.setFill(y == yHi ? Theme.GREEN : Theme.RED);
                gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                gc.fillText(y == yHi ? "↑ rising" : "↓ falling", x, y == yHi ? yHi - 14 : yLo + 14);
            }
            if (t >= 0 || y != yLo) gc.strokeLine(x, y, x + 2, y);
            prevY = y;
        }
        gc.setStroke(Theme.AMBER);
        gc.setLineWidth(2);
        gc.setLineDashes(5, 4);
        gc.strokeLine(nowX, 20, nowX, 165);
        gc.setLineDashes();
        gc.setFill(Theme.AMBER);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("now", nowX, 12);
        info.setText(String.format("Frequency %.1f Hz  |  period %.2f s  |  pulses counted so far: %d", f, 1 / f, clock.getPulseCount()));
    }

    @Override
    public Node diagram() {
        return Kit.label("The clock is a square wave. Each cycle has a HIGH half and a LOW half. The change from 0 to 1 is the RISING edge, the change from 1 to 0 is the FALLING edge. "
                + "Frequency f (in Hz) is the number of cycles per second and the period is T = 1 / f.", "body-text");
    }

    @Override
    public Node interactive() {
        Label fl = Kit.plain("Speed", "lab-title");
        HBox bar = new HBox(12, Kit.button("⏯ Run / Pause", "btn-primary", e -> running = !running), fl, speed,
            Kit.button("Reset counter", "btn-secondary", e -> { clock.reset(); time = 0; wasHigh = false; }));
        bar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return new VBox(12, Kit.label("The waveform scrolls to the left. The counter below uses the project's Clock class and goes up by one at every rising edge.", "muted"), bar, canvas, info);
    }

    @Override
    public void dispose() { timer.stop(); }
}
