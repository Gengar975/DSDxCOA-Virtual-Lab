package playground;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CircuitTest {

    private static boolean gate(ComponentType type, boolean a, boolean b) {
        Circuit c = new Circuit();
        String in1 = c.addComponent(ComponentType.INPUT, 0, 0);
        String in2 = c.addComponent(ComponentType.INPUT, 0, 0);
        String g = c.addComponent(type, 0, 0);
        String out = c.addComponent(ComponentType.OUTPUT, 0, 0);
        c.connect(in1, g, 0);
        c.connect(in2, g, 1);
        c.connect(g, out, 0);
        c.setInputValue(in1, a);
        c.setInputValue(in2, b);
        return c.run().getValue(out);
    }

    @Test
    public void twoInputGatesMatchTruthTables() {
        boolean[][] in = {{false, false}, {false, true}, {true, false}, {true, true}};
        boolean[] and = {false, false, false, true};
        boolean[] or = {false, true, true, true};
        boolean[] nand = {true, true, true, false};
        boolean[] nor = {true, false, false, false};
        boolean[] xor = {false, true, true, false};
        boolean[] xnor = {true, false, false, true};
        for (int i = 0; i < 4; i++) {
            assertEquals(and[i], gate(ComponentType.AND, in[i][0], in[i][1]));
            assertEquals(or[i], gate(ComponentType.OR, in[i][0], in[i][1]));
            assertEquals(nand[i], gate(ComponentType.NAND, in[i][0], in[i][1]));
            assertEquals(nor[i], gate(ComponentType.NOR, in[i][0], in[i][1]));
            assertEquals(xor[i], gate(ComponentType.XOR, in[i][0], in[i][1]));
            assertEquals(xnor[i], gate(ComponentType.XNOR, in[i][0], in[i][1]));
        }
    }

    @Test
    public void inputNotOutput() {
        Circuit c = new Circuit();
        String in = c.addComponent(ComponentType.INPUT, 0, 0);
        String not = c.addComponent(ComponentType.NOT, 0, 0);
        String out = c.addComponent(ComponentType.OUTPUT, 0, 0);
        c.connect(in, not, 0);
        c.connect(not, out, 0);
        c.setInputValue(in, false);
        assertTrue(c.run().getValue(out));
        c.setInputValue(in, true);
        assertFalse(c.run().getValue(out));
    }

    @Test
    public void multiStageAndNot() {
        Circuit c = PrebuiltCircuits.nandFromAndNot();
        String a = "INPUT_1", b = "INPUT_2", out = "OUTPUT_1";
        for (int i = 0; i < 4; i++) {
            boolean va = (i & 2) != 0, vb = (i & 1) != 0;
            c.setInputValue(a, va);
            c.setInputValue(b, vb);
            assertEquals(!(va && vb), c.run().getValue(out));
        }
    }

    @Test
    public void stepsAreInSignalFlowOrderAndExplainable() {
        Circuit c = PrebuiltCircuits.twoInput(ComponentType.AND);
        c.setInputValue("INPUT_1", true);
        c.setInputValue("INPUT_2", true);
        SimulationResult r = c.run();
        assertEquals(4, r.steps().size());
        assertEquals(ComponentType.OUTPUT, r.steps().get(3).type());
        ComponentState and = r.getState("AND_1");
        assertEquals(java.util.List.of(true, true), and.inputValues());
        assertEquals("AND_1 receives 1, 1 -> produces 1", and.describe());
        assertSame(r, c.getLastResult());
    }

    @Test
    public void invalidConnectionsAreRejected() {
        Circuit c = new Circuit();
        String in = c.addComponent(ComponentType.INPUT, 0, 0);
        String in2 = c.addComponent(ComponentType.INPUT, 0, 0);
        String not = c.addComponent(ComponentType.NOT, 0, 0);
        String not2 = c.addComponent(ComponentType.NOT, 0, 0);
        String out = c.addComponent(ComponentType.OUTPUT, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> c.connect(not, not, 0));      // self
        assertThrows(IllegalArgumentException.class, () -> c.connect(out, not, 0));      // OUTPUT has no output
        assertThrows(IllegalArgumentException.class, () -> c.connect(in, in2, 0));       // INPUT has no input
        assertThrows(IllegalArgumentException.class, () -> c.connect(in, not, 1));       // bad port
        assertThrows(IllegalArgumentException.class, () -> c.connect("nope", not, 0));   // unknown
        c.connect(in, not, 0);
        assertThrows(IllegalArgumentException.class, () -> c.connect(in2, not, 0));      // port already used
        c.connect(not, not2, 0);
        assertThrows(IllegalArgumentException.class, () -> c.connect(not2, not, 0));     // loop
        assertEquals(2, c.getConnections().size());
    }

    @Test
    public void incompleteCircuitCannotRun() {
        Circuit c = new Circuit();
        c.addComponent(ComponentType.INPUT, 0, 0);
        c.addComponent(ComponentType.AND, 0, 0);
        c.addComponent(ComponentType.OUTPUT, 0, 0);
        assertFalse(c.validate().isEmpty());
        assertThrows(IllegalStateException.class, c::run);
    }

    @Test
    public void moveRemoveAndDisconnect() {
        Circuit c = new Circuit();
        String in = c.addComponent(ComponentType.INPUT, 1, 2);
        String out = c.addComponent(ComponentType.OUTPUT, 0, 0);
        c.connect(in, out, 0);
        c.moveComponent(in, 10, 20);
        assertEquals(new Position(10, 20), c.getComponent(in).getPosition());
        c.disconnect(out, 0);
        assertTrue(c.getConnections().isEmpty());
        c.connect(in, out, 0);
        c.removeComponent(in);
        assertTrue(c.getConnections().isEmpty());
        assertEquals(1, c.getComponents().size());
        assertThrows(IllegalArgumentException.class, () -> c.disconnect(out, 0));
    }

    @Test
    public void resetClearsInputsAndResultButKeepsStructure() {
        Circuit c = PrebuiltCircuits.twoInput(ComponentType.OR);
        c.setInputValue("INPUT_1", true);
        c.run();
        c.reset();
        assertNull(c.getLastResult());
        assertFalse(c.getComponent("INPUT_1").getInputValue());
        assertEquals(4, c.getComponents().size());
        assertEquals(3, c.getConnections().size());
        assertFalse(c.run().getValue("OUTPUT_1"));
    }

    @Test
    public void clearRemovesEverything() {
        Circuit c = PrebuiltCircuits.combo();
        c.clear();
        assertTrue(c.getComponents().isEmpty());
        assertTrue(c.getConnections().isEmpty());
    }

    @Test
    public void prebuiltCircuitsEvaluateCorrectly() {
        for (int i = 0; i < 4; i++) {
            boolean a = (i & 2) != 0, b = (i & 1) != 0;
            check("AND", a, b, a && b);
            check("OR", a, b, a || b);
            check("XOR", a, b, a ^ b);
            check("NAND (NOT of AND)", a, b, !(a && b));
            check("NOT-AND-NOR combo", a, b, a && !b);
        }
        assertEquals(5, PrebuiltCircuits.names().size());
        assertThrows(IllegalArgumentException.class, () -> PrebuiltCircuits.byName("???"));
    }

    private static void check(String name, boolean a, boolean b, boolean expected) {
        Circuit c = PrebuiltCircuits.byName(name);
        c.setInputValue("INPUT_1", a);
        c.setInputValue("INPUT_2", b);
        assertEquals(expected, c.run().getValue("OUTPUT_1"), name + " a=" + a + " b=" + b);
    }
}
