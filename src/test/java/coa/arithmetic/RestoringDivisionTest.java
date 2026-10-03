package coa.arithmetic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestoringDivisionTest {

    @Test
    public void testPositiveDivision() {
        DivisionResult res = RestoringDivision.divide(13, 4, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testExactDivision() {
        DivisionResult res = RestoringDivision.divide(100, 10, 8);
        assertEquals(10, res.getQuotient());
        assertEquals(0, res.getRemainder());
    }

    @Test
    public void testNonExactDivision() {
        DivisionResult res = RestoringDivision.divide(7, 2, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testNegativeDividend() {
        DivisionResult res = RestoringDivision.divide(-13, 4, 8);
        assertEquals(-3, res.getQuotient());
        assertEquals(-1, res.getRemainder());
    }

    @Test
    public void testNegativeDivisor() {
        DivisionResult res = RestoringDivision.divide(13, -4, 8);
        assertEquals(-3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testBothNegative() {
        DivisionResult res = RestoringDivision.divide(-13, -4, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(-1, res.getRemainder());
    }

    @Test
    public void testZeroDividend() {
        DivisionResult res = RestoringDivision.divide(0, 5, 8);
        assertEquals(0, res.getQuotient());
        assertEquals(0, res.getRemainder());
    }

    @Test
    public void testDivisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> RestoringDivision.divide(10, 0, 8));
    }

    @Test
    public void testBoundaryValue() {
        // Largest 8-bit unsigned magnitude.
        DivisionResult res = RestoringDivision.divide(255, 1, 8);
        assertEquals(255, res.getQuotient());
        assertEquals(0, res.getRemainder());
    }

    @Test
    public void testOperandTooLargeThrows() {
        assertThrows(IllegalArgumentException.class, () -> RestoringDivision.divide(1000, 1, 8));
    }
}
