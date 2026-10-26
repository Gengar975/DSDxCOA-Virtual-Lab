package lab.ui;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * A row of bit cells. Cells that change value pop (scale) and flash yellow, so students can see
 * exactly which bits an operation touched.
 */
final class BitCells extends HBox {

    private String bits = "";
    private final String[] zones;   // optional style class per cell (sign / exp / man)
    private final boolean small;
    private boolean dimZeros = false;

    BitCells(int width, boolean small) {
        this(width, small, null);
    }

    BitCells(int width, boolean small, String[] zones) {
        super(small ? 4 : 6);
        this.small = small;
        this.zones = zones;
        setAlignment(Pos.CENTER_LEFT);
        StringBuilder zeros = new StringBuilder();
        for (int i = 0; i < width; i++) zeros.append('0');
        set(zeros.toString(), false);
    }

    String get() { return bits; }

    /** Draws 0 bits faded so the 1 bits stand out (used for the 32-bit IEEE 754 row). */
    void setDimZeros(boolean dim) { this.dimZeros = dim; }

    /** Shows a new value. When animate is true, changed cells flash. */
    void set(String value, boolean animate) {
        boolean rebuild = getChildren().size() != value.length();
        if (rebuild) getChildren().clear();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            Label cell;
            if (rebuild) {
                cell = new Label();
                getChildren().add(cell);
            } else {
                cell = (Label) getChildren().get(i);
            }
            boolean changed = animate && !rebuild && i < bits.length() && bits.charAt(i) != c;
            cell.setText(String.valueOf(c));
            cell.setOpacity(dimZeros && c == '0' ? 0.55 : 1.0);
            cell.getStyleClass().removeAll("bit", "bit-1", "bit-sign", "bit-exp", "bit-man", "bit-small", "bit-changed");
            cell.getStyleClass().add("bit");
            if (small) cell.getStyleClass().add("bit-small");
            if (zones != null && i < zones.length && zones[i] != null) cell.getStyleClass().add(zones[i]);
            else if (c == '1') cell.getStyleClass().add("bit-1");
            if (changed) {
                cell.getStyleClass().add("bit-changed");
                ScaleTransition pop = new ScaleTransition(Duration.millis(220), cell);
                pop.setFromX(1.0);
                pop.setToX(1.25);
                pop.setFromY(1.0);
                pop.setToY(1.25);
                pop.setCycleCount(2);
                pop.setAutoReverse(true);
                pop.play();
                PauseTransition settle = new PauseTransition(Duration.millis(900));
                final Label target = cell;
                settle.setOnFinished(e -> target.getStyleClass().remove("bit-changed"));
                settle.play();
            }
        }
        bits = value;
    }
}
