package lab.ui;

import dsd.combinational.BcdAdder;
import dsd.combinational.Decoder3to8;
import dsd.combinational.MagnitudeComparator;
import dsd.combinational.Multiplexer4to1;
import dsd.combinational.PriorityEncoder8to3;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Lab for chip-style combinational circuits (decoder, multiplexer, priority encoder, comparator,
 * BCD adder). The values come straight from the project's circuit classes.
 */
final class BlockLab implements Lab {

    private final String blackBoxNote;
    private final BlockView symbol;
    private final BlockView view;
    private final TruthGrid grid;
    private final Function<Map<String, Boolean>, Integer> rowFor;
    private final Function<Map<String, Boolean>, String> detail;
    private final Label detailLabel = Kit.plain("", "formula");

    private BlockLab(String title, List<String> ins, List<String> outs,
                     Function<Map<String, Boolean>, Map<String, Boolean>> logic,
                     Function<Map<String, Boolean>, Set<String>> active,
                     List<String> headers, int outputCols, List<String[]> rows,
                     Function<Map<String, Boolean>, Integer> rowFor,
                     Function<Map<String, Boolean>, String> detail, String note) {
        this.blackBoxNote = note;
        this.symbol = new BlockView(title, ins, outs, false, null, null);
        this.view = new BlockView(title, ins, outs, true, logic, active);
        this.grid = new TruthGrid(headers, outputCols, rows);
        this.rowFor = rowFor;
        this.detail = detail;
        view.setOnChange(this::sync);
        sync();
    }

    private void sync() {
        Map<String, Boolean> in = view.inputs();
        if (view.getError() == null) grid.highlight(rowFor.apply(in)); else grid.highlight(-1);
        detailLabel.setText(detail == null || view.getError() != null ? "" : detail.apply(in));
    }

    @Override public Node diagram() { return new VBox(12, Kit.label(blackBoxNote, "body-text"), symbol); }

    @Override public Node table() { return grid; }

    @Override
    public Node interactive() {
        return new VBox(12, Kit.label("Click the input switches. A pulse travels into the chip and out to the lamps; the matching row of the table is highlighted.", "muted"), view, detailLabel);
    }

    @Override
    public void dispose() {
        symbol.stop();
        view.stop();
    }

    // ------------------------------------------------------------------ factories

    private static boolean b(Map<String, Boolean> m, String k) { return m.getOrDefault(k, false); }

    private static int num(Map<String, Boolean> m, String... bitsMsbFirst) {
        int v = 0;
        for (String k : bitsMsbFirst) v = (v << 1) | (b(m, k) ? 1 : 0);
        return v;
    }

    static BlockLab decoder() {
        List<String> ins = List.of("E", "A2", "A1", "A0");
        List<String> outs = new ArrayList<>();
        for (int i = 0; i < 8; i++) outs.add("Y" + i);
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"0", "X", "X", "X", "none"});
        for (int i = 0; i < 8; i++) rows.add(new String[] {"1", String.valueOf(i >> 2 & 1), String.valueOf(i >> 1 & 1), String.valueOf(i & 1), "Y" + i});
        Decoder3to8 d = new Decoder3to8();
        return new BlockLab("3-to-8 DECODER", ins, outs,
            m -> d.compute(new LinkedHashMap<>(m)),
            m -> Set.of(),
            List.of("E", "A2", "A1", "A0", "Active output"), 1, rows,
            m -> b(m, "E") ? 1 + num(m, "A2", "A1", "A0") : 0,
            m -> b(m, "E") ? "Code A2 A1 A0 = " + num(m, "A2", "A1", "A0") + " (binary " + (b(m, "A2") ? 1 : 0) + (b(m, "A1") ? 1 : 0) + (b(m, "A0") ? 1 : 0) + ") → output Y" + num(m, "A2", "A1", "A0") + " is 1"
                              : "ENABLE (E) is 0, so every output stays 0",
            "A decoder has 3 code inputs (A2 A1 A0), an ENABLE input E, and 8 output lines Y0 - Y7. Exactly one output is 1: the line whose number equals the input code.");
    }

    static BlockLab mux() {
        List<String> ins = List.of("I0", "I1", "I2", "I3", "S1", "S0");
        Multiplexer4to1 mux = new Multiplexer4to1();
        List<String[]> rows = List.of(
            new String[] {"0", "0", "I0"}, new String[] {"0", "1", "I1"},
            new String[] {"1", "0", "I2"}, new String[] {"1", "1", "I3"});
        return new BlockLab("4-to-1 MUX", ins, List.of("Y"),
            m -> mux.compute(new LinkedHashMap<>(m)),
            m -> Set.of("I" + num(m, "S1", "S0"), "S1", "S0"),
            List.of("S1", "S0", "Y equals"), 1, rows,
            m -> num(m, "S1", "S0"),
            m -> "Select S1 S0 = " + num(m, "S1", "S0") + " → the amber wire (I" + num(m, "S1", "S0") + ") is passed to the output Y",
            "A multiplexer has 4 data inputs (I0 - I3), 2 select lines (S1 S0) and one output Y. The select lines choose which data input is copied to Y.");
    }

    static BlockLab priority() {
        List<String> ins = new ArrayList<>();
        for (int i = 0; i < 8; i++) ins.add("D" + i);
        PriorityEncoder8to3 pe = new PriorityEncoder8to3();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"0 0 0 0 0 0 0 0", "X X X", "0"});
        for (int h = 0; h < 8; h++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 7; i >= 0; i--) sb.append(i > h ? "0" : i == h ? "1" : "X").append(i > 0 ? " " : "");
            rows.add(new String[] {sb.toString(), String.valueOf(h >> 2 & 1) + " " + (h >> 1 & 1) + " " + (h & 1), "1"});
        }
        return new BlockLab("8-to-3 PRIORITY ENCODER", ins, List.of("A2", "A1", "A0", "VALID"),
            m -> pe.compute(new LinkedHashMap<>(m)),
            m -> {
                for (int i = 7; i >= 0; i--) if (b(m, "D" + i)) return Set.of("D" + i);
                return Set.of();
            },
            List.of("D7 ... D0", "A2 A1 A0", "VALID"), 2, rows,
            m -> {
                for (int i = 7; i >= 0; i--) if (b(m, "D" + i)) return i + 1;
                return 0;
            },
            m -> {
                for (int i = 7; i >= 0; i--) if (b(m, "D" + i)) return "Highest active input is D" + i + " (amber) → output code " + i;
                return "No input is active, so VALID = 0";
            },
            "An 8-to-3 priority encoder reports the NUMBER of the highest-numbered input that is 1 as a 3-bit code (A2 A1 A0). VALID tells whether any input is active. X in the table means 'don't care'.");
    }

    static BlockLab comparator() {
        List<String> ins = List.of("A3", "A2", "A1", "A0", "B3", "B2", "B1", "B0");
        List<String[]> rows = List.of(
            new String[] {"A > B", "1", "0", "0"}, new String[] {"A = B", "0", "1", "0"}, new String[] {"A < B", "0", "0", "1"});
        return new BlockLab("4-BIT COMPARATOR", ins, List.of("A>B", "A=B", "A<B"),
            m -> {
                MagnitudeComparator.Result r = MagnitudeComparator.compare4Bit(num(m, "A3", "A2", "A1", "A0"), num(m, "B3", "B2", "B1", "B0"));
                Map<String, Boolean> o = new LinkedHashMap<>();
                o.put("A>B", r.aGreaterThanB);
                o.put("A=B", r.aEqualsB);
                o.put("A<B", r.aLessThanB);
                return o;
            }, m -> Set.of(),
            List.of("Case", "A>B", "A=B", "A<B"), 3, rows,
            m -> { int a = num(m, "A3", "A2", "A1", "A0"), c = num(m, "B3", "B2", "B1", "B0"); return a > c ? 0 : a == c ? 1 : 2; },
            m -> "A = " + num(m, "A3", "A2", "A1", "A0") + ",  B = " + num(m, "B3", "B2", "B1", "B0"),
            "A comparator looks at two 4-bit numbers A and B and turns on exactly one of three outputs: A > B, A = B or A < B. Compare from the most significant bit down.");
    }

    static BlockLab bcdAdder() {
        List<String> ins = List.of("A3", "A2", "A1", "A0", "B3", "B2", "B1", "B0", "CIN");
        List<String[]> rows = List.of(
            new String[] {"0 - 9", "no", "binary sum is already a valid BCD digit"},
            new String[] {"10 - 19", "yes", "add 0110 (6) and send a carry to the next digit"});
        return new BlockLab("BCD ADDER", ins, List.of("COUT", "S3", "S2", "S1", "S0"),
            m -> {
                BcdAdder.BcdResult r = BcdAdder.add(num(m, "A3", "A2", "A1", "A0"), num(m, "B3", "B2", "B1", "B0"), b(m, "CIN"));
                Map<String, Boolean> o = new LinkedHashMap<>();
                o.put("COUT", r.carryOut);
                for (int i = 3; i >= 0; i--) o.put("S" + i, ((r.correctedBcdSum >> i) & 1) == 1);
                return o;
            }, m -> Set.of(),
            List.of("Binary sum", "Correction?", "What happens"), 2, rows,
            m -> BcdAdder.add(num(m, "A3", "A2", "A1", "A0"), num(m, "B3", "B2", "B1", "B0"), b(m, "CIN")).correctionApplied ? 1 : 0,
            m -> {
                BcdAdder.BcdResult r = BcdAdder.add(num(m, "A3", "A2", "A1", "A0"), num(m, "B3", "B2", "B1", "B0"), b(m, "CIN"));
                return "Binary sum = " + r.rawSum + (r.correctionApplied ? "  → greater than 9, add 6  → digit " + (r.correctedBcdSum & 0xF) + " with carry 1" : "  → valid BCD digit, no correction");
            },
            "A BCD adder adds two decimal digits (each stored in 4 bits, valid values 0 - 9). If the binary sum is above 9, 6 is added to skip the unused codes. Note: switch combinations above 9 are not valid BCD and will show a warning.");
    }
}
