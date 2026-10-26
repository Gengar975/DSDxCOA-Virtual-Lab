package lab.ui;

import coa.memory.Memory;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Main memory: 16 addressable locations. Write and read operations highlight the selected cell. */
final class MemoryLab implements Lab {

    private static final int SIZE = 16;

    private final Memory memory = new Memory(SIZE);
    private final Label[] cell = new Label[SIZE];
    private final Label[] addr = new Label[SIZE];
    private final VBox[] box = new VBox[SIZE];
    private final TextField address = Kit.field("address 0-15", 110);
    private final TextField data = Kit.field("value", 110);
    private final Label status = Kit.label("", "status-info");
    private final Label bus = Kit.plain("CPU  ──  address bus  ──▶  [ — ]  ◀── data bus ──  MEMORY", "formula");

    MemoryLab() {
        address.setText("3");
        data.setText("42");
        for (int i = 0; i < SIZE; i++) {
            addr[i] = Kit.plain("addr " + i, "reg-value");
            cell[i] = Kit.plain("0", "card-title");
            box[i] = new VBox(2, addr[i], cell[i]);
            box[i].getStyleClass().add("lab-panel");
            box[i].setAlignment(Pos.CENTER);
            box[i].setMinWidth(104);
        }
    }

    private void refresh(int hot) {
        for (int i = 0; i < SIZE; i++) {
            cell[i].setText(String.valueOf(memory.read(i)));
            box[i].getStyleClass().remove("callout");
        }
        if (hot >= 0) {
            box[hot].getStyleClass().add("callout");
            ScaleTransition pop = new ScaleTransition(Duration.millis(220), box[hot]);
            pop.setFromX(1);
            pop.setToX(1.12);
            pop.setFromY(1);
            pop.setToY(1.12);
            pop.setCycleCount(2);
            pop.setAutoReverse(true);
            pop.play();
            PauseTransition fade = new PauseTransition(Duration.millis(1600));
            fade.setOnFinished(e -> box[hot].getStyleClass().remove("callout"));
            fade.play();
        }
    }

    private void op(boolean write) {
        try {
            int a = Integer.parseInt(address.getText().trim());
            bus.setText("CPU  ──  address " + a + "  ──▶  [ cell " + a + " ]  " + (write ? "◀── data " + data.getText().trim() + " ──  write" : "── data ──▶  CPU  read"));
            if (write) {
                memory.write(a, Long.parseLong(data.getText().trim()));
                status.setText("Wrote " + data.getText().trim() + " into address " + a + ". The old value was replaced.");
            } else {
                long v = memory.read(a);
                status.setText("Read address " + a + ": the value is " + v + ". Reading does not change the memory.");
            }
            status.getStyleClass().setAll("status-info");
            refresh(a);
        } catch (NumberFormatException ex) {
            status.setText("✖ Please type whole numbers.");
            status.getStyleClass().setAll("status-bad");
        } catch (RuntimeException ex) {
            status.setText("✖ " + ex.getMessage());
            status.getStyleClass().setAll("status-bad");
        }
    }

    @Override
    public Node diagram() {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        for (int i = 0; i < SIZE; i++) g.add(box[i], i % 8, i / 8);
        return new VBox(12, Kit.label("Memory is a numbered list of cells. The number is the ADDRESS; the content is the stored VALUE. 16 cells are shown (addresses 0 - 15).", "body-text"), g);
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10, Kit.plain("Address", "lab-title"), address, Kit.plain("Value", "lab-title"), data,
            Kit.button("Write", "btn-primary", e -> op(true)), Kit.button("Read", "btn-secondary", e -> op(false)));
        bar.setAlignment(Pos.CENTER_LEFT);
        return new VBox(14, Kit.label("Write a value into an address, then read it back. The cells in section 3 light up when they are used.", "muted"), bar, bus, status);
    }
}
