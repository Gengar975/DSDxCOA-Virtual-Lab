package lab.ui;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lab.content.Catalog;
import lab.content.Topic;

/** The welcome screen: three entry tiles and the full learning path. */
final class HomePage extends VBox {

    HomePage(Consumer<Topic> open) {
        getStyleClass().add("page");
        VBox hero = new VBox(8);
        hero.getStyleClass().add("hero");
        hero.getChildren().addAll(
            Kit.plain("WELCOME", "hero-crumb"),
            Kit.plain("DigiCore Lab", "hero-title"),
            Kit.label("Learn Digital System Design and Computer Organization step by step: read the idea, study the diagram and the truth table, then play with an animated circuit.", "hero-sub"));

        HBox tiles = new HBox(18,
            tile("D", "tile-dsd", "DSD Lab", "Logic gates, Boolean algebra, number systems, combinational and sequential circuits.", () -> open.accept(Catalog.byId("and"))),
            tile("C", "tile-coa", "COA Lab", "Binary arithmetic, floating point, ALU, registers, memory and cache.", () -> open.accept(Catalog.byId("booth"))),
            tile("▶", "tile-pg", "Circuit Playground", "Drag and drop gates on a canvas and build your own circuits.", () -> open.accept(Catalog.byId("playground"))));
        for (var node : tiles.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        VBox inner = new VBox(22);
        inner.getStyleClass().add("page-inner");
        inner.getChildren().add(tiles);

        VBox path = new VBox(18);
        for (Map.Entry<Topic.Group, List<Topic>> e : Catalog.byGroup().entrySet()) {
            if (e.getKey() == Topic.Group.PLAYGROUND) continue;
            FlowPane chips = new FlowPane(8, 8);
            for (Topic t : e.getValue()) {
                Label c = Kit.plain(t.getTitle(), "chip-dark");
                c.setOnMouseClicked(ev -> open.accept(t));
                chips.getChildren().add(c);
            }
            path.getChildren().add(new VBox(8, Kit.plain(e.getKey().getLab() + "  ›  " + e.getKey().getTitle(), "lab-title"), chips));
        }
        inner.getChildren().add(Kit.section(1, "Learning path", "Start at the top and work your way down - each topic builds on the ones before it", path));
        getChildren().addAll(hero, inner);
        setPadding(new Insets(0, 0, 40, 0));
    }

    private static VBox tile(String icon, String style, String title, String text, Runnable go) {
        Label i = Kit.plain(icon, "tile-icon");
        i.getStyleClass().add(style);
        VBox t = new VBox(12, i, Kit.plain(title, "tile-title"), Kit.label(text, "muted"));
        t.getStyleClass().add("tile");
        t.setOnMouseClicked(e -> go.run());
        t.setMaxWidth(Double.MAX_VALUE);
        return t;
    }
}
