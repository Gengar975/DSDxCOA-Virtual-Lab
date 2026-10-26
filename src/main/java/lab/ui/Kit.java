package lab.ui;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Small factory methods so the screens read like a description of the layout. */
final class Kit {

    private Kit() { }

    static Label label(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        l.setWrapText(true);
        return l;
    }

    static Label plain(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }

    static Button button(String text, String styleClass, EventHandler<ActionEvent> action) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        b.setOnAction(action);
        return b;
    }

    static ToggleButton bitToggle(String name, EventHandler<ActionEvent> action) {
        ToggleButton t = new ToggleButton(name + " = 0");
        t.getStyleClass().add("toggle-bit");
        t.setOnAction(e -> {
            t.setText(name + " = " + (t.isSelected() ? "1" : "0"));
            action.handle(e);
        });
        return t;
    }

    static TextField field(String prompt, double width) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.getStyleClass().add("input-field");
        f.setPrefWidth(width);
        return f;
    }

    static ComboBox<String> combo(String... items) {
        ComboBox<String> c = new ComboBox<>();
        c.getItems().addAll(items);
        c.setValue(items[0]);
        return c;
    }

    static HBox row(double spacing, Node... nodes) {
        HBox h = new HBox(spacing, nodes);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    static VBox column(double spacing, Node... nodes) {
        return new VBox(spacing, nodes);
    }

    static Region2 grow() {
        Region2 r = new Region2();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /** A white card with a numbered title, as used for every section of a lesson. */
    static VBox section(int number, String title, String subtitle, Node content) {
        Label n = new Label(String.valueOf(number));
        n.getStyleClass().add("section-number");
        VBox titles = new VBox(2, plain(title, "card-title"));
        if (subtitle != null && !subtitle.isEmpty()) titles.getChildren().add(label(subtitle, "card-sub"));
        HBox head = new HBox(12, n, titles);
        head.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(16, head, content);
        card.getStyleClass().add("card");
        return card;
    }

    static VBox panel(String title, Node... content) {
        VBox p = new VBox(10);
        p.getStyleClass().add("lab-panel");
        if (title != null) p.getChildren().add(plain(title, "lab-title"));
        p.getChildren().addAll(content);
        return p;
    }

    static VBox bullets(List<String> items) {
        VBox box = new VBox(8);
        for (String s : items) {
            Label dot = plain("•", "bullet-dot");
            Label text = label(s, "bullet");
            HBox.setHgrow(text, Priority.ALWAYS);
            HBox line = new HBox(10, dot, text);
            line.setAlignment(Pos.TOP_LEFT);
            box.getChildren().add(line);
        }
        return box;
    }

    static Insets pad(double all) {
        return new Insets(all);
    }

    /** Tiny spacer node that grows (named to avoid clashing with javafx.scene.layout.Region imports). */
    static final class Region2 extends javafx.scene.layout.Region { }
}
