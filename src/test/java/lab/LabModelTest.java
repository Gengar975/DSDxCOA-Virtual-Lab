package lab;

import static org.junit.jupiter.api.Assertions.*;

import dsd.combinational.FullAdder;
import dsd.combinational.FullSubtractor;
import dsd.combinational.HalfAdder;
import dsd.combinational.HalfSubtractor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lab.content.Catalog;
import lab.content.Topic;
import lab.model.CircuitLibrary;
import lab.model.CircuitTable;
import lab.model.KMapAnalyzer;
import lab.model.StepRegisters;
import org.junit.jupiter.api.Test;
import playground.Circuit;

class LabModelTest {

    private static Map<String, Boolean> in(String k1, boolean v1, String k2, boolean v2) {
        return Map.of(k1, v1, k2, v2);
    }

    @Test
    void halfAdderDiagramMatchesEngineClass() {
        CircuitTable t = new CircuitTable(CircuitLibrary.halfAdder());
        for (int r = 0; r < t.rowCount(); r++) {
            boolean[] i = t.inputs(r), o = t.outputs(r);
            Map<String, Boolean> expect = new HalfAdder().compute(in("A", i[0], "B", i[1]));
            assertEquals(expect.get("SUM"), o[0]);
            assertEquals(expect.get("CARRY"), o[1]);
        }
    }

    @Test
    void fullAdderDiagramMatchesEngineClass() {
        CircuitTable t = new CircuitTable(CircuitLibrary.fullAdder());
        assertEquals(8, t.rowCount());
        for (int r = 0; r < 8; r++) {
            boolean[] i = t.inputs(r), o = t.outputs(r);
            Map<String, Boolean> expect = new FullAdder().compute(Map.of("A", i[0], "B", i[1], "CIN", i[2]));
            assertEquals(expect.get("SUM"), o[0], "row " + r);
            assertEquals(expect.get("COUT"), o[1], "row " + r);
        }
    }

    @Test
    void subtractorDiagramsMatchEngineClasses() {
        CircuitTable h = new CircuitTable(CircuitLibrary.halfSubtractor());
        for (int r = 0; r < h.rowCount(); r++) {
            boolean[] i = h.inputs(r), o = h.outputs(r);
            Map<String, Boolean> e = new HalfSubtractor().compute(in("A", i[0], "B", i[1]));
            assertEquals(e.get("DIFF"), o[0]);
            assertEquals(e.get("BORROW"), o[1]);
        }
        CircuitTable f = new CircuitTable(CircuitLibrary.fullSubtractor());
        for (int r = 0; r < f.rowCount(); r++) {
            boolean[] i = f.inputs(r), o = f.outputs(r);
            int a = i[0] ? 1 : 0, b = i[1] ? 1 : 0, bin = i[2] ? 1 : 0;
            int diff = a - b - bin;
            Map<String, Boolean> e = new FullSubtractor().compute(Map.of("A", i[0], "B", i[1], "BIN", i[2]));
            assertEquals((diff & 1) == 1, o[0], "diff row " + r);
            assertEquals(diff < 0, o[1], "borrow row " + r);
            assertEquals(e.get("DIFF"), o[0]);
            assertEquals(e.get("BOUT"), o[1]);
        }
    }

    @Test
    void circuitTableRestoresSwitches() {
        Circuit c = CircuitLibrary.halfAdder();
        String a = c.getComponents().get(0).getId();
        c.setInputValue(a, true);
        new CircuitTable(c);
        assertTrue(c.getComponent(a).getInputValue());
    }

    @Test
    void everyGateTopicHasADiagram() {
        for (String id : List.of("and", "or", "not", "nand", "nor", "xor", "xnor", "half-adder", "full-adder", "half-sub", "full-sub")) {
            assertTrue(CircuitLibrary.hasDiagram(id), id);
            assertDoesNotThrow(() -> new CircuitTable(CircuitLibrary.forTopic(id)));
        }
    }

    @Test
    void kmapGroupsSimplify() {
        List<KMapAnalyzer.Group> g = KMapAnalyzer.analyze(3, Set.of(1, 3));
        assertEquals(1, g.size());
        assertEquals("A'C", g.get(0).term());
        assertEquals("1", KMapAnalyzer.analyze(2, Set.of(0, 1, 2, 3)).get(0).term());
        // wrap-around: minterms 0 and 2 on a 3-variable map are neighbours
        assertEquals("A'C'", KMapAnalyzer.analyze(3, Set.of(0, 2)).get(0).term());
        // 4 corners of a 4-variable map form one group B'D'
        assertEquals("B'D'", KMapAnalyzer.analyze(4, Set.of(0, 2, 8, 10)).get(0).term());
        assertEquals("0", KMapAnalyzer.expression(List.of(), Set.of()));
    }

    @Test
    void kmapExpressionIsEquivalentToTheFunction() {
        // brute force: every function of 3 variables
        for (int mask = 0; mask < 256; mask++) {
            Set<Integer> ones = new java.util.TreeSet<>();
            for (int m = 0; m < 8; m++) if (((mask >> m) & 1) == 1) ones.add(m);
            List<KMapAnalyzer.Group> groups = KMapAnalyzer.analyze(3, ones);
            Set<Integer> covered = new java.util.TreeSet<>();
            for (KMapAnalyzer.Group g : groups) covered.addAll(g.minterms());
            assertEquals(ones, covered, "mask " + mask);
        }
    }

    @Test
    void stepRegistersAreParsed() {
        Map<String, String> m = StepRegisters.parse("Iteration 1: after shift, A = 00001, Q = 1010 | A - M = 11110");
        assertEquals("00001", m.get("A"));
        assertEquals("1010", m.get("Q"));
        assertEquals("1000", StepRegisters.parse("Initial: M (multiplicand) = 1000 (-8)").get("M"));
        assertEquals("0", StepRegisters.parse("Initial: A = 0000, Q-1 = 0").get("Q-1"));
        assertTrue(StepRegisters.parse("Final: Quotient magnitude = 4").isEmpty());
    }

    @Test
    void catalogIsCompleteAndConsistent() {
        List<Topic> all = Catalog.all();
        assertEquals(all.size(), all.stream().map(Topic::getId).distinct().count(), "ids are unique");
        for (Topic t : all) {
            assertFalse(t.getTheory().isEmpty(), t.getId());
            assertFalse(t.getTagline().isBlank(), t.getId());
            if (t.getGroup() != Topic.Group.PLAYGROUND) {
                assertFalse(t.getKeyPoints().isEmpty(), t.getId());
                assertFalse(t.getExampleLines().isEmpty(), t.getId());
            }
        }
        for (Topic.Group g : Topic.Group.values()) assertFalse(Catalog.byGroup().get(g).isEmpty(), g.name());
    }
}
