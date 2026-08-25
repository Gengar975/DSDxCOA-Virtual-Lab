package dsd;

import dsd.gates.AndGate;
import dsd.gates.OrGate;
import dsd.gates.NotGate;
import dsd.gates.XorGate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogicGatesTest {

    @Test
    public void testAndGate() {
        AndGate and = new AndGate();
        assertFalse(and.evaluate(false, false));
        assertFalse(and.evaluate(false, true));
        assertFalse(and.evaluate(true, false));
        assertTrue(and.evaluate(true, true));
    }

    @Test
    public void testOrGate() {
        OrGate or = new OrGate();
        assertFalse(or.evaluate(false, false));
        assertTrue(or.evaluate(false, true));
        assertTrue(or.evaluate(true, false));
        assertTrue(or.evaluate(true, true));
    }

    @Test
    public void testNotGate() {
        NotGate not = new NotGate();
        assertTrue(not.evaluate(false));
        assertFalse(not.evaluate(true));
    }

    @Test
    public void testXorGate() {
        XorGate xor = new XorGate();
        assertFalse(xor.evaluate(false, false));
        assertTrue(xor.evaluate(true, false));
        assertTrue(xor.evaluate(false, true));
        assertFalse(xor.evaluate(true, true));
    }
}