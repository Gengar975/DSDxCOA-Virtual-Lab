package lab.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Finds the groups of 1s on a Karnaugh map (wrap-around rectangles of 1, 2, 4, 8 or 16 cells)
 * and turns them into a simplified Sum-of-Products expression.
 */
public final class KMapAnalyzer {

    private static final int[] GRAY2 = {0, 1, 3, 2};
    private static final String[] VARS = {"A", "B", "C", "D"};

    /** One circled group: the minterms it covers and its product term. */
    public record Group(List<Integer> minterms, String term) { }

    private KMapAnalyzer() { }

    public static int rows(int n) { return n == 4 ? 4 : 2; }
    public static int cols(int n) { return n == 2 ? 2 : 4; }

    /** Minterm number shown in grid cell (r, c) - same layout as {@code KMapSolver}. */
    public static int mintermAt(int n, int r, int c) {
        if (n == 2) return (r << 1) | c;
        if (n == 3) return (r << 2) | GRAY2[c];
        return (GRAY2[r] << 2) | GRAY2[c];
    }

    public static List<Group> analyze(int n, Set<Integer> ones) {
        if (n < 2 || n > 4) throw new IllegalArgumentException("K-Map supports 2, 3 or 4 variables.");
        int rows = rows(n), cols = cols(n);
        Set<Set<Integer>> found = new LinkedHashSet<>();
        for (int h = 1; h <= rows; h *= 2) {
            for (int w = 1; w <= cols; w *= 2) {
                for (int r0 = 0; r0 < rows; r0++) {
                    for (int c0 = 0; c0 < cols; c0++) {
                        Set<Integer> cells = new TreeSet<>();
                        boolean allOnes = true;
                        for (int i = 0; i < h && allOnes; i++) {
                            for (int j = 0; j < w; j++) {
                                int m = mintermAt(n, (r0 + i) % rows, (c0 + j) % cols);
                                if (!ones.contains(m)) { allOnes = false; break; }
                                cells.add(m);
                            }
                        }
                        if (allOnes) found.add(cells);
                    }
                }
            }
        }
        // keep only maximal groups
        List<Set<Integer>> maximal = new ArrayList<>();
        for (Set<Integer> g : found) {
            boolean inside = false;
            for (Set<Integer> o : found) if (o != g && o.size() > g.size() && o.containsAll(g)) { inside = true; break; }
            if (!inside) maximal.add(g);
        }
        // choose a small cover: essential groups first, then greedily the one covering most
        Set<Integer> uncovered = new TreeSet<>(ones);
        List<Set<Integer>> chosen = new ArrayList<>();
        for (int m : new TreeSet<>(ones)) {
            List<Set<Integer>> covering = new ArrayList<>();
            for (Set<Integer> g : maximal) if (g.contains(m)) covering.add(g);
            if (covering.size() == 1 && !chosen.contains(covering.get(0))) {
                chosen.add(covering.get(0));
                uncovered.removeAll(covering.get(0));
            }
        }
        while (!uncovered.isEmpty()) {
            Set<Integer> best = null;
            int bestGain = 0;
            for (Set<Integer> g : maximal) {
                if (chosen.contains(g)) continue;
                Set<Integer> gain = new TreeSet<>(g);
                gain.retainAll(uncovered);
                if (gain.size() > bestGain) { best = g; bestGain = gain.size(); }
            }
            if (best == null) break;
            chosen.add(best);
            uncovered.removeAll(best);
        }
        List<Group> out = new ArrayList<>();
        for (Set<Integer> g : chosen) out.add(new Group(new ArrayList<>(g), term(n, g)));
        return out;
    }

    /** Product term of a group: variables that never change stay, the others disappear. */
    static String term(int n, Set<Integer> group) {
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < n; v++) {
            int bit = n - 1 - v;   // A is the most significant bit
            boolean sawOne = false, sawZero = false;
            for (int m : group) {
                if (((m >> bit) & 1) == 1) sawOne = true; else sawZero = true;
            }
            if (sawOne && !sawZero) sb.append(VARS[v]);
            else if (sawZero && !sawOne) sb.append(VARS[v]).append('\'');
        }
        return sb.length() == 0 ? "1" : sb.toString();
    }

    /** Simplified SOP expression, e.g. "A'C + B". */
    public static String expression(List<Group> groups, Set<Integer> ones) {
        if (ones.isEmpty()) return "0";
        List<String> terms = new ArrayList<>();
        for (Group g : groups) terms.add(g.term());
        Collections.sort(terms);
        return String.join(" + ", terms);
    }
}
