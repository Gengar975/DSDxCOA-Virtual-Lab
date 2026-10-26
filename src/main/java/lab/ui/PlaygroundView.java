package lab.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lab.model.CircuitLibrary;
import lab.model.CircuitTable;
import playground.Circuit;
import playground.ComponentType;
import playground.PrebuiltCircuits;

/**
 * The circuit playground: a drag-and-drop canvas. Components are dragged from the palette onto the
 * canvas, moved around, wired output-to-input, and the signals animate through the circuit. A live
 * truth table (every input combination run through the simulation) can be shown below.
 */
final class PlaygroundView extends BorderPane {

    private static final String[][] PALETTE = {
        {"INPUT", "Input switch", "click to switch 0 / 1"},
        {"OUTPUT", "Output lamp", "shows the result"},
        {"AND", "AND gate", "1 only if both are 1"},
        {"OR", "OR gate", "1 if either is 1"},
        {"NOT", "NOT gate", "inverts the input"},
        {"NAND", "NAND gate", "NOT of AND"},
        {"NOR", "NOR gate", "NOT of OR"},
        {"XOR", "XOR gate", "1 if inputs differ"},
        {"XNOR", "XNOR gate", "1 if inputs equal"}};

    private final EditableCircuitView view = new EditableCircuitView(new Circuit());
    private final Label status = Kit.label("Drag components from the left onto the canvas.", "status-info");
    private final VBox tableBox = new VBox(10);
    private boolean tableVisible = false;

    PlaygroundView() {
        getStyleClass().add("page");
        setLeft(buildPalette());
        setTop(buildToolbar());
        ScrollPane canvasScroll = new ScrollPane(view);
        canvasScroll.getStyleClass().add("canvas-frame");
        canvasScroll.setPadding(new Insets(0));
        VBox center = new VBox(12, canvasScroll, status, tableBox);
        center.setPadding(new Insets(16));
        VBox.setVgrow(canvasScroll, Priority.ALWAYS);
        ScrollPane page = new ScrollPane(center);
        page.setFitToWidth(true);
        page.getStyleClass().add("page-scroll");
        setCenter(page);

        view.setMessages(status::setText);
        view.setOnChange(this::afterChange);
        view.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });
        view.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean ok = false;
            if (db.hasString()) {
                view.addComponent(ComponentType.valueOf(db.getString()), e.getX(), e.getY());
                status.setText("Added " + db.getString() + ". Drag from its small output dot (right) to another component's input dot (left) to wire it.");
                ok = true;
            }
            e.setDropCompleted(ok);
            e.consume();
        });
        view.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) view.deleteSelected();
        });
    }

    private Node buildPalette() {
        VBox box = new VBox(8);
        box.getStyleClass().add("palette");
        box.getChildren().add(Kit.plain("COMPONENTS  (drag onto the canvas)", "palette-title"));
        for (String[] p : PALETTE) {
            Label item = new Label(p[1]);
            item.getStyleClass().add("palette-item");
            item.setMaxWidth(Double.MAX_VALUE);
            final String type = p[0];
            item.setOnDragDetected(e -> {
                Dragboard db = item.startDragAndDrop(TransferMode.COPY);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(type);
                db.setContent(cc);
                e.consume();
            });
            // a double click also adds the component, for students who prefer not to drag
            item.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) view.addComponent(ComponentType.valueOf(type), 120 + Math.random() * 400, 80 + Math.random() * 300);
            });
            Label hint = Kit.plain(p[2], "muted");
            box.getChildren().add(new VBox(1, item, hint));
        }
        box.getChildren().add(Kit.label("Tip: double-click a component to add it without dragging.", "muted"));
        box.setPrefWidth(190);
        return box;
    }

    private Node buildToolbar() {
        List<String> names = new ArrayList<>(PrebuiltCircuits.names());
        names.add("Half adder");
        names.add("Full adder");
        ComboBox<String> examples = new ComboBox<>();
        examples.getItems().addAll(names);
        examples.setPromptText("Ready-made circuits…");
        examples.setOnAction(e -> {
            String n = examples.getValue();
            if (n == null) return;
            Circuit c = n.equals("Half adder") ? CircuitLibrary.halfAdder() : n.equals("Full adder") ? CircuitLibrary.fullAdder() : PrebuiltCircuits.byName(n);
            view.setCircuit(c);
            status.setText("Loaded \"" + n + "\". Click the inputs to flip them, or drag parts to rearrange.");
        });
        HBox bar = new HBox(10, examples,
            Kit.button("🗑 Delete selected", "btn-secondary", e -> view.deleteSelected()),
            Kit.button("Reset inputs to 0", "btn-secondary", e -> view.resetInputs()),
            Kit.button("Clear canvas", "btn-danger", e -> { view.clearAll(); status.setText("Canvas cleared."); }),
            Kit.button("📋 Show / hide truth table", "btn-primary", e -> { tableVisible = !tableVisible; afterChange(); }));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("toolbar");
        return bar;
    }

    /** Called after every change: refreshes the table and the status line. */
    private void afterChange() {
        List<String> problems = view.getProblems();
        if (!problems.isEmpty() && !view.circuit().getComponents().isEmpty()) status.setText("⚠ " + problems.get(0));
        tableBox.getChildren().clear();
        if (!tableVisible) return;
        try {
            CircuitTable t = new CircuitTable(view.circuit());
            List<String> head = new ArrayList<>(t.getInputLabels());
            head.addAll(t.getOutputLabels());
            List<String[]> rows = new ArrayList<>();
            for (int r = 0; r < t.rowCount(); r++) {
                List<String> row = new ArrayList<>();
                for (boolean b : t.inputs(r)) row.add(b ? "1" : "0");
                for (boolean b : t.outputs(r)) row.add(b ? "1" : "0");
                rows.add(row.toArray(new String[0]));
            }
            TruthGrid g = new TruthGrid(head, t.getOutputLabels().size(), rows);
            g.highlight(t.rowFor(view.circuit()));
            tableBox.getChildren().addAll(Kit.plain("Truth table of your circuit (the highlighted row matches the current switches)", "lab-title"), g);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            tableBox.getChildren().add(Kit.label("Truth table: " + ex.getMessage(), "muted"));
        }
    }

    void dispose() { view.stop(); }
}
