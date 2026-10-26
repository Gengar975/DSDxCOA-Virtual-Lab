package lab.ui;

import coa.memory.Cache;
import coa.memory.CacheAccessResult;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Direct-mapped cache with 4 lines of 4 bytes and 6-bit addresses. The address is split into
 * tag | index | offset, the selected line is highlighted and the access shows HIT or MISS. The
 * project's Cache class decides hit or miss; a mirror of its tags is kept only to draw the table.
 */
final class CacheLab implements Lab {

    private static final int LINES = 4, LINE_BYTES = 4;

    private Cache cache = new Cache(LINES, LINE_BYTES);
    private final int[] tags = new int[LINES];
    private final boolean[] valid = new boolean[LINES];
    private final TextField address = Kit.field("address 0-63", 110);
    private final BitCells addrBits;
    private final Label verdict = Kit.plain("", "card-title");
    private final Label stats = Kit.plain("", "formula");
    private final VBox tableHolder = new VBox();
    private final VBox stepsBox = new VBox(4);
    private final ComboBox<String> sequences = Kit.combo("Sequential 0 … 15", "Loop 0,1,2,3 repeated", "Conflict 0,16,0,16 (same line)");
    private int hits, misses, hotLine = -1;
    private Boolean lastHit;
    private Timeline seq;

    CacheLab() {
        String[] zones = {"bit-exp", "bit-exp", "bit-man", "bit-man", "bit-sign", "bit-sign"};
        addrBits = new BitCells(6, false, zones);
        address.setText("37");
        address.setOnAction(e -> access(parse()));
        drawTable();
    }

    private int parse() {
        try {
            int a = Integer.parseInt(address.getText().trim());
            if (a < 0 || a > 63) throw new NumberFormatException();
            return a;
        } catch (NumberFormatException ex) {
            verdict.setText("✖ Type an address from 0 to 63");
            return -1;
        }
    }

    private void access(int a) {
        if (a < 0) return;
        CacheAccessResult r = cache.access(a);
        String bin = String.format("%6s", Integer.toBinaryString(a)).replace(' ', '0');
        addrBits.set(bin, true);
        hotLine = r.getIndex();
        lastHit = r.isHit();
        if (r.isHit()) hits++; else { misses++; valid[r.getIndex()] = true; tags[r.getIndex()] = r.getTag(); }
        verdict.setText(r.isHit() ? "✔ HIT   - the data was already in the cache" : "✖ MISS  - loaded from main memory into line " + r.getIndex());
        verdict.getStyleClass().setAll("card-title", r.isHit() ? "status-ok" : "status-bad");
        stats.setText("Address " + a + " = " + bin + "  →  tag " + r.getTag() + " | index " + r.getIndex() + " | offset " + r.getOffset()
                + "\nAccesses: " + (hits + misses) + "   hits: " + hits + "   misses: " + misses
                + "   hit rate: " + (hits + misses == 0 ? 0 : Math.round(100.0 * hits / (hits + misses))) + "%");
        stepsBox.getChildren().clear();
        for (String s : r.getSteps()) stepsBox.getChildren().add(Kit.label("• " + s, "bullet"));
        drawTable();
    }

    private void drawTable() {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < LINES; i++)
            rows.add(new String[] {String.valueOf(i), valid[i] ? "1" : "0", valid[i] ? String.valueOf(tags[i]) : "-",
                valid[i] ? "block " + (tags[i] * LINES + i) + "  (addresses " + (tags[i] * LINES + i) * LINE_BYTES + " - " + ((tags[i] * LINES + i) * LINE_BYTES + LINE_BYTES - 1) + ")" : "empty"});
        TruthGrid g = new TruthGrid(List.of("Line (index)", "Valid", "Tag", "Holds"), 0, rows);
        g.highlight(hotLine);
        tableHolder.getChildren().setAll(g);
    }

    private void reset() {
        if (seq != null) seq.stop();
        cache = new Cache(LINES, LINE_BYTES);
        for (int i = 0; i < LINES; i++) { valid[i] = false; tags[i] = 0; }
        hits = 0;
        misses = 0;
        hotLine = -1;
        verdict.setText("");
        stats.setText("");
        stepsBox.getChildren().clear();
        addrBits.set("000000", false);
        drawTable();
    }

    private void runSequence() {
        reset();
        int[] list = switch (sequences.getSelectionModel().getSelectedIndex()) {
            case 0 -> java.util.stream.IntStream.range(0, 16).toArray();
            case 1 -> new int[] {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
            default -> new int[] {0, 16, 0, 16, 0, 16};
        };
        seq = new Timeline();
        for (int i = 0; i < list.length; i++) {
            final int a = list[i];
            seq.getKeyFrames().add(new KeyFrame(Duration.millis(900 * (i + 1)), e -> { address.setText(String.valueOf(a)); access(a); }));
        }
        seq.play();
    }

    @Override
    public Node diagram() {
        HBox key = new HBox(10);
        for (String[] k : new String[][] {{"TAG (2 bits)", "bit-exp"}, {"INDEX (2 bits)", "bit-man"}, {"OFFSET (2 bits)", "bit-sign"}}) {
            Label l = Kit.plain(k[0], k[1]);
            l.getStyleClass().add("bit");
            l.setMinWidth(0);
            l.setStyle("-fx-font-size: 12px; -fx-padding: 6 12 6 12;");
            key.getChildren().add(l);
        }
        return new VBox(12, Kit.label("This cache has 4 lines of 4 bytes each. A 6-bit address is split into three fields: the OFFSET picks the byte inside a line, the INDEX picks the cache line, and the TAG tells which memory block currently lives in that line.", "body-text"), key);
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10, Kit.plain("Address", "lab-title"), address, Kit.button("Access", "btn-primary", e -> access(parse())),
            Kit.button("Empty cache", "btn-secondary", e -> reset()));
        bar.setAlignment(Pos.CENTER_LEFT);
        HBox seqBar = new HBox(10, Kit.plain("Or watch a pattern:", "lab-title"), sequences, Kit.button("Run pattern", "btn-secondary", e -> runSequence()));
        seqBar.setAlignment(Pos.CENTER_LEFT);
        HBox labels = new HBox(6);
        for (String s : new String[] {"tag", "tag", "idx", "idx", "off", "off"}) {
            Label l = Kit.plain(s, "reg-value");
            l.setMinWidth(26);
            l.setAlignment(Pos.CENTER);
            labels.getChildren().add(l);
        }
        return new VBox(14, Kit.label("Access an address. The first access to a block is always a MISS; accessing it again is a HIT. Try 37, then 36, then 53.", "muted"),
            bar, seqBar, labels, addrBits, verdict, tableHolder, stats, Kit.plain("What happened", "lab-title"), stepsBox);
    }

    @Override
    public void dispose() {
        if (seq != null) seq.stop();
    }
}
