package dsd;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import dsd.combinational.BcdAdder;
import dsd.combinational.Decoder3to8;
import dsd.combinational.FullSubtractor;
import dsd.combinational.HalfSubtractor;
import dsd.combinational.PriorityEncoder8to3;

public class AdvancedCombinationalTest {

    @Test
    public void testHalfSubtractor() {
        HalfSubtractor hs = new HalfSubtractor();
        Map<String, Boolean> out = hs.compute(Map.of("A", false, "B", true)); // 0 - 1
        assertTrue(out.get("DIFF"));
        assertTrue(out.get("BORROW"));
    }

    @Test
    public void testFullSubtractor() {
        FullSubtractor fs = new FullSubtractor();
        Map<String, Boolean> out = fs.compute(Map.of("A", true, "B", false, "BIN", true)); // 1 - 0 - 1
        assertFalse(out.get("DIFF"));
        assertFalse(out.get("BOUT"));
    }

    @Test
    public void testDecoder3to8() {
        Decoder3to8 decoder = new Decoder3to8();
        Map<String, Boolean> out = decoder.compute(Map.of("E", true, "A2", true, "A1", false, "A0", true)); // 5 (101)
        assertTrue(out.get("Y5"));
        assertFalse(out.get("Y0"));
    }

    @Test
    public void testPriorityEncoder() {
        PriorityEncoder8to3 encoder = new PriorityEncoder8to3();
        Map<String, Boolean> out = encoder.compute(Map.of("D2", true, "D6", true)); // D6 should take priority
        assertTrue(out.get("VALID"));
        assertTrue(out.get("A2"));
        assertTrue(out.get("A1"));
        assertFalse(out.get("A0")); // 6 in binary is 110
    }

    @Test
    public void testBcdAdderWithCorrection() {
        BcdAdder.BcdResult res = BcdAdder.add(7, 8, false); // 7 + 8 = 15 (> 9, add 6 -> 21 = 0x15 -> BCD 5 with carry 1)
        assertTrue(res.correctionApplied);
        assertTrue(res.carryOut);
        assertEquals(5, res.correctedBcdSum);
    }
}