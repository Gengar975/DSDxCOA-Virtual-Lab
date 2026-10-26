package lab.ui;

import javafx.scene.paint.Color;

/** Colours shared by the canvas drawings (the CSS file uses the same palette). */
final class Theme {

    private Theme() { }

    static final Color INK = Color.web("#0f172a");
    static final Color TEXT = Color.web("#1e293b");
    static final Color MUTED = Color.web("#64748b");
    static final Color INDIGO = Color.web("#4f46e5");
    static final Color INDIGO_LIGHT = Color.web("#eef2ff");
    static final Color GREEN = Color.web("#16a34a");
    static final Color GREEN_LIGHT = Color.web("#dcfce7");
    static final Color RED = Color.web("#dc2626");
    static final Color AMBER = Color.web("#f59e0b");
    static final Color BLUE = Color.web("#2563eb");
    static final Color WIRE_OFF = Color.web("#94a3b8");
    static final Color GATE_FILL = Color.web("#e0e7ff");
    static final Color GATE_LINE = Color.web("#3730a3");
    static final Color PANEL = Color.web("#f8fafc");
    static final Color BORDER = Color.web("#cbd5e1");
    static final Color WHITE = Color.WHITE;

    /** Distinct colours for K-map groups. */
    static final Color[] GROUPS = {
        Color.web("#ef4444"), Color.web("#2563eb"), Color.web("#16a34a"), Color.web("#f59e0b"),
        Color.web("#9333ea"), Color.web("#0891b2"), Color.web("#db2777"), Color.web("#65a30d")
    };
}
