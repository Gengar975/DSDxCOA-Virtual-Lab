package coa.alu;

import coa.arithmetic.BitUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AluTest {

    private final Alu alu = new Alu();

    @Test
    public void testAddition() {
        AluResult res = alu.compute(BitUtils.toBits(5, 8), BitUtils.toBits(3, 8), AluOperation.ADD);
        assertEquals(8, BitUtils.toSignedLong(binaryStringToBits(res.getResultBinary())));
        assertFalse(res.isZeroFlag());
    }

    @Test
    public void testSubtraction() {
        AluResult res = alu.compute(BitUtils.toBits(7, 8), BitUtils.toBits(4, 8), AluOperation.SUB);
        assertEquals(3, BitUtils.toSignedLong(binaryStringToBits(res.getResultBinary())));
    }

    @Test
    public void testAnd() {
        AluResult res = alu.compute(BitUtils.toBits(0b1100, 8), BitUtils.toBits(0b1010, 8), AluOperation.AND);
        assertEquals("00001000", res.getResultBinary());
    }

    @Test
    public void testOr() {
        AluResult res = alu.compute(BitUtils.toBits(0b1100, 8), BitUtils.toBits(0b1010, 8), AluOperation.OR);
        assertEquals("00001110", res.getResultBinary());
    }

    @Test
    public void testXor() {
        AluResult res = alu.compute(BitUtils.toBits(0b1100, 8), BitUtils.toBits(0b1010, 8), AluOperation.XOR);
        assertEquals("00000110", res.getResultBinary());
    }

    @Test
    public void testNot() {
        AluResult res = alu.compute(BitUtils.toBits(0, 8), AluOperation.NOT);
        assertEquals("11111111", res.getResultBinary());
    }

    @Test
    public void testZeroResult() {
        AluResult res = alu.compute(BitUtils.toBits(5, 8), BitUtils.toBits(5, 8), AluOperation.SUB);
        assertTrue(res.isZeroFlag());
    }

    @Test
    public void testCarryFlagOnAddition() {
        // 255 + 1 in 8-bit unsigned wraps and produces a carry out.
        boolean[] a = new boolean[]{true, true, true, true, true, true, true, true}; // 11111111
        boolean[] b = BitUtils.toBits(1, 8);
        AluResult res = alu.compute(a, b, AluOperation.ADD);
        assertTrue(res.isCarryFlag());
        assertTrue(res.isZeroFlag());
    }

    @Test
    public void testOverflowFlag() {
        // 5-bit example mirrors existing dsd test style: 5 (0101) + 3 (0011) overflows 4-bit signed range.
        AluResult res = alu.compute(BitUtils.toBits(5, 4), BitUtils.toBits(3, 4), AluOperation.ADD);
        assertTrue(res.isOverflowFlag());
    }

    @Test
    public void testNotOperationRejectedForBinaryCompute() {
        assertThrows(IllegalArgumentException.class,
            () -> alu.compute(BitUtils.toBits(1, 8), BitUtils.toBits(1, 8), AluOperation.NOT));
    }

    private boolean[] binaryStringToBits(String s) {
        boolean[] bits = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bits[i] = s.charAt(i) == '1';
        }
        return bits;
    }
}
