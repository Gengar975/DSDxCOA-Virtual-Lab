package coa.arithmetic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoothMultiplierTest {

    @Test
    public void testPositiveTimesPositive() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(5, 3, 8);
        assertEquals(15, res.getProduct());
        assertFalse(res.getSteps().isEmpty());
    }

    @Test
    public void testNegativeTimesPositive() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(-5, 3, 8);
        assertEquals(-15, res.getProduct());
    }

    @Test
    public void testPositiveTimesNegative() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(5, -3, 8);
        assertEquals(-15, res.getProduct());
    }

    @Test
    public void testNegativeTimesNegative() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(-5, -3, 8);
        assertEquals(15, res.getProduct());
    }

    @Test
    public void testMultiplicationByZero() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(0, 42, 8);
        assertEquals(0, res.getProduct());

        BoothMultiplicationResult res2 = BoothMultiplier.multiply(42, 0, 8);
        assertEquals(0, res2.getProduct());
    }

    @Test
    public void testBoundaryValues() {
        // -8 * -8 = 64, the largest magnitude product for 4-bit signed operands.
        BoothMultiplicationResult res = BoothMultiplier.multiply(-8, -8, 4);
        assertEquals(64, res.getProduct());
    }

    @Test
    public void testOperandOutOfRangeThrows() {
        assertThrows(IllegalArgumentException.class, () -> BoothMultiplier.multiply(200, 1, 8));
    }

    @Test
    public void testStepsRecordEachIteration() {
        BoothMultiplicationResult res = BoothMultiplier.multiply(3, 3, 8);
        // 3 initial steps + 8 iteration steps + 1 final step = 12
        assertEquals(12, res.getSteps().size());
    }
}
