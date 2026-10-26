package lab.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Plays a list of calculation steps one at a time (Prev / Play / Next / Restart).
 * The current step is highlighted, earlier steps are shown in dark text and later ones are faded,
 * and a callback tells the screen which step is now active so it can animate registers or bits.
 */
final class StepPlayer extends VBox {

    private final List<Label> rows = new ArrayList<>();
    private final VBox list = new VBox(3);
    private final Label counter = Kit.plain("", "muted");
    private final Button play;
    private final Timeline timeline;
    private IntConsumer onStep = i -> { };
    private int current = -1;

    StepPlayer(List<String> steps, double listHeight) {
        super(10);
        for (String s : steps) {
            Label l = new Label(s);
            l.getStyleClass().add("step-row");
            l.setWrapText(true);
            l.setMaxWidth(Double.MAX_VALUE);
            rows.add(l);
            list.getChildren().add(l);
        }
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(listHeight);
        scroll.setMinHeight(listHeight);

        Button restart = Kit.button("⏮ Restart", "btn-secondary", e -> goTo(-1));
        Button prev = Kit.button("◀ Back", "btn-secondary", e -> { pause(); goTo(current - 1); });
        play = Kit.button("▶ Play", "btn-primary", e -> togglePlay());
        Button next = Kit.button("Next ▶", "btn-secondary", e -> { pause(); goTo(current + 1); });
        Button all = Kit.button("Show all", "btn-secondary", e -> { pause(); goTo(rows.size() - 1); });
        HBox controls = new HBox(8, restart, prev, play, next, all, counter);
        controls.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(controls, scroll);

        timeline = new Timeline(new KeyFrame(Duration.millis(1100), e -> {
            if (current + 1 >= rows.size()) pause();
            else goTo(current + 1);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        goTo(-1);
    }

    void setOnStep(IntConsumer c) { this.onStep = c; }

    int size() { return rows.size(); }

    void stop() { pause(); }

    /** Starts playing from the beginning. */
    void playFromStart() {
        goTo(-1);
        timeline.play();
        play.setText("⏸ Pause");
    }

    private void togglePlay() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            pause();
        } else {
            if (current + 1 >= rows.size()) goTo(-1);
            timeline.play();
            play.setText("⏸ Pause");
        }
    }

    private void pause() {
        timeline.stop();
        play.setText("▶ Play");
    }

    void goTo(int index) {
        if (index < -1) index = -1;
        if (index >= rows.size()) index = rows.size() - 1;
        current = index;
        for (int i = 0; i < rows.size(); i++) {
            Label l = rows.get(i);
            l.getStyleClass().removeAll("step-done", "step-current");
            if (i < current) l.getStyleClass().add("step-done");
            if (i == current) l.getStyleClass().add("step-current");
        }
        counter.setText(current < 0 ? "  press Play or Next" : "  step " + (current + 1) + " of " + rows.size());
        onStep.accept(current);
    }
}
