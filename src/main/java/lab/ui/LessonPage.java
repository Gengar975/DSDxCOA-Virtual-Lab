package lab.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lab.content.Topic;

/**
 * One full-screen lesson, top to bottom: header, theory and key points, a worked example, the
 * diagram, the truth / function table, the animated "try it yourself" lab, and real-world uses.
 */
final class LessonPage extends VBox {

    private final Lab lab;

    LessonPage(Topic topic) {
        getStyleClass().add("page");
        setSpacing(0);
        this.lab = topic.getGroup() == Topic.Group.PLAYGROUND ? null : Labs.create(topic);

        VBox hero = new VBox(6);
        hero.getStyleClass().add("hero");
        hero.getChildren().addAll(
            Kit.plain(topic.getGroup().getLab() + " LAB   ›   " + topic.getGroup().getTitle().toUpperCase(), "hero-crumb"),
            Kit.plain(topic.getTitle(), "hero-title"),
            Kit.label(topic.getTagline(), "hero-sub"));
        HBox chips = new HBox(8, Kit.plain("Beginner friendly", "chip"), Kit.plain("Theory", "chip"), Kit.plain("Animated lab", "chip"));
        chips.setPadding(new Insets(10, 0, 0, 0));
        hero.getChildren().add(chips);

        VBox inner = new VBox(22);
        inner.getStyleClass().add("page-inner");
        int n = 1;

        // 1. theory + key points
        VBox theory = new VBox(12);
        for (String p : topic.getTheory()) theory.getChildren().add(Kit.label(p, "body-text"));
        if (!topic.getKeyPoints().isEmpty()) {
            theory.getChildren().add(Kit.plain("Key points to remember", "lab-title"));
            theory.getChildren().add(Kit.bullets(topic.getKeyPoints()));
        }
        inner.getChildren().add(Kit.section(n++, "What is it?", "The idea in plain words", theory));

        // 2. worked example
        if (!topic.getExampleLines().isEmpty()) {
            VBox ex = new VBox(6);
            ex.getStyleClass().add("callout");
            ex.getChildren().add(Kit.plain(topic.getExampleTitle(), "callout-title"));
            for (String line : topic.getExampleLines()) ex.getChildren().add(Kit.label(line, "example-line"));
            inner.getChildren().add(Kit.section(n++, "Worked example", "Follow the steps with pen and paper first", ex));
        }

        if (lab != null) {
            Node diagram = lab.diagram();
            if (diagram != null) inner.getChildren().add(Kit.section(n++, "Diagram", "How it looks and how it is built", diagram));
            Node table = lab.table();
            if (table != null) inner.getChildren().add(Kit.section(n++, Labs.tableTitle(topic), "Every input combination and its result", table));
            Node live = lab.interactive();
            if (live != null) inner.getChildren().add(Kit.section(n++, "Try it yourself", "Interactive and animated - change the inputs and watch what happens", live));
        }

        if (!topic.getUsedIn().isEmpty())
            inner.getChildren().add(Kit.section(n, "Where is it used?", "Real-world applications", Kit.bullets(topic.getUsedIn())));
        getChildren().addAll(hero, inner);
        setAlignment(Pos.TOP_LEFT);
    }

    void dispose() {
        if (lab != null) lab.dispose();
    }
}
