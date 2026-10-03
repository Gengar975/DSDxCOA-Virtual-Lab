package dsd;

import dsd.combinational.FullAdder;
import dsd.combinational.HalfAdder;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CombinationalTest {

    @Test
    public void testHalfAdder() {
        HalfAdder ha = new HalfAdder();
        Map<String, Boolean> out = ha.compute(Map.of("A", true, "B", true));
        assertFalse(out.get("SUM"));
        assertTrue(out.get("CARRY"));
    }

    @Test
    public void testFullAdder() {
        FullAdder fa = new FullAdder();
        Map<String, Boolean> out = fa.compute(Map.of("A", true, "B", true, "CIN", true));
        assertTrue(out.get("SUM"));
        assertTrue(out.get("COUT"));
    }
}