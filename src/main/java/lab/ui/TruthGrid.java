package lab.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/** A table (truth table, function table, state table) with row highlighting and a reveal animation. */
final class TruthGrid extends GridPane {

    private final List<List<Label>> rows = new ArrayList<>();
    private int highlighted = -1;

    /**
     * @param headers   column headings
     * @param outputCols number of columns, counted from the right, that are outputs (drawn in the accent colour)
     * @param data      table body; each row has one string per column
     */
    TruthGrid(List<String> headers, int outputCols, List<String[]> data) {
        setHgap(0);
        setVgap(0);
        for (int c = 0; c < headers.size(); c++) {
            Label h = new Label(headers.get(c));
            h.getStyleClass().add("tt-head");
            if (c >= headers.size() - outputCols) h.getStyleClass().add("tt-head-out");
            h.setMaxWidth(Double.MAX_VALUE);
            add(h, c, 0);
        }
        for (int r = 0; r < data.size(); r++) {
            List<Label> line = new ArrayList<>();
            String[] row = data.get(r);
            for (int c = 0; c < row.length; c++) {
                Label cell = new Label(row[c]);
                cell.getStyleClass().add("tt-cell");
                if (c >= headers.size() - outputCols) cell.getStyleClass().add("tt-cell-out");
                if ("1".equals(row[c])) cell.getStyleClass().add("tt-one");
                cell.setMaxWidth(Double.MAX_VALUE);
                add(cell, c, r + 1);
                line.add(cell);
            }
            rows.add(line);
        }
    }

    int rowCount() { return rows.size(); }

    /** Highlights one row (or none with -1). */
    void highlight(int row) {
        if (highlighted >= 0 && highlighted < rows.size()) {
            for (Label l : rows.get(highlighted)) l.getStyleClass().remove("tt-hi");
        }
        highlighted = row;
        if (row >= 0 && row < rows.size()) {
            for (Label l : rows.get(row)) l.getStyleClass().add("tt-hi");
        }
    }

    /** Rows appear one after another, which helps students follow the table being built. */
    void reveal(double millisPerRow) {
        for (List<Label> line : rows) for (Label l : line) l.setOpacity(0);
        Timeline t = new Timeline();
        for (int r = 0; r < rows.size(); r++) {
            final List<Label> line = rows.get(r);
            t.getKeyFrames().add(new KeyFrame(Duration.millis(millisPerRow * (r + 1)), e -> {
                for (Label l : line) l.setOpacity(1);
            }));
        }
        t.play();
    }
}
