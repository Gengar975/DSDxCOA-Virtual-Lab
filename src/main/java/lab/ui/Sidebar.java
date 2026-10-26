package lab.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lab.content.Catalog;
import lab.content.Topic;

/** The dark navigation column: brand, search box, and the topics grouped by section. */
final class Sidebar extends VBox {

    private final Map<Button, Topic> items = new LinkedHashMap<>();
    private final Map<Label, List<Button>> groups = new LinkedHashMap<>();
    private final Button home = new Button("⌂  Home");
    private Button selected;

    Sidebar(Runnable openHome, Consumer<Topic> open) {
        getStyleClass().add("sidebar");
        setPrefWidth(290);
        setMinWidth(290);

        VBox brand = new VBox(2, Kit.plain("DigiCore Lab", "brand-title"), Kit.plain("Digital logic & computer organization", "brand-sub"));
        brand.getStyleClass().add("brand-box");

        TextField search = new TextField();
        search.setPromptText("Search topics…");
        search.getStyleClass().add("search-box");
        VBox.setMargin(search, new Insets(0, 16, 8, 16));

        home.getStyleClass().addAll("nav-item", "nav-home");
        home.setMaxWidth(Double.MAX_VALUE);
        home.setOnAction(e -> openHome.run());

        VBox list = new VBox();
        for (Map.Entry<Topic.Group, List<Topic>> e : Catalog.byGroup().entrySet()) {
            Label title = Kit.plain(e.getKey().getTitle().toUpperCase(), "nav-group-title");
            list.getChildren().add(title);
            List<Button> buttons = new ArrayList<>();
            for (Topic t : e.getValue()) {
                Button b = new Button(t.getTitle());
                b.getStyleClass().add("nav-item");
                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(ev -> open.accept(t));
                items.put(b, t);
                buttons.add(b);
                list.getChildren().add(b);
            }
            groups.put(title, buttons);
        }
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("nav-scroll");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

        search.textProperty().addListener((o, a, text) -> filter(text));
        getChildren().addAll(brand, search, home, scroll);
    }

    private void filter(String text) {
        String q = text == null ? "" : text.trim().toLowerCase();
        for (Map.Entry<Label, List<Button>> g : groups.entrySet()) {
            boolean any = false;
            for (Button b : g.getValue()) {
                Topic t = items.get(b);
                boolean match = q.isEmpty() || t.getTitle().toLowerCase().contains(q) || t.getTagline().toLowerCase().contains(q) || t.getId().contains(q);
                b.setVisible(match);
                b.setManaged(match);
                any |= match;
            }
            g.getKey().setVisible(any);
            g.getKey().setManaged(any);
        }
    }

    /** Marks the entry for this topic (or Home when null) as selected. */
    void select(Topic topic) {
        if (selected != null) selected.getStyleClass().remove("selected");
        selected = null;
        if (topic == null) {
            selected = home;
        } else {
            for (Map.Entry<Button, Topic> e : items.entrySet()) if (e.getValue() == topic) selected = e.getKey();
        }
        if (selected != null) selected.getStyleClass().add("selected");
    }
}
