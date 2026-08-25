package dsd;

import dsd.combinational.AluArithmeticResult;
import dsd.combinational.RippleCarryAdderSubtractor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RippleCarryAdderSubtractorTest {

    @Test
    public void test4BitAdditionNoOverflow() {
        RippleCarryAdderSubtractor rca = new RippleCarryAdderSubtractor();
        // 5 (0101) + 3 (0011) = 8 (1000)
        AluArithmeticResult res = rca.compute("0101", "0011", false);
        assertEquals("1000", res.getBinaryString());
        assertFalse(res.isZeroFlag());
        assertTrue(res.isSignFlag());     // 1000 in 4-bit 2's complement is negative (-8)
        assertFalse(res.isCarryFlag());
        assertTrue(res.isOverflowFlag()); // 5 + 3 = 8 overflows 4-bit signed range [-8, 7]
    }

    @Test
    public void test4BitSubtraction() {
        RippleCarryAdderSubtractor rca = new RippleCarryAdderSubtractor();
        // 7 (0111) - 4 (0100) = 3 (0011)
        AluArithmeticResult res = rca.compute("0111", "0100", true);
        assertEquals("0011", res.getBinaryString());
        assertFalse(res.isZeroFlag());
        assertFalse(res.isSignFlag());
        assertFalse(res.isOverflowFlag());
    }

    @Test
    public void testZeroFlag() {
        RippleCarryAdderSubtractor rca = new RippleCarryAdderSubtractor();
        // 4 (0100) - 4 (0100) = 0 (0000)
        AluArithmeticResult res = rca.compute("0100", "0100", true);
        assertEquals("0000", res.getBinaryString());
        assertTrue(res.isZeroFlag());
    }
}