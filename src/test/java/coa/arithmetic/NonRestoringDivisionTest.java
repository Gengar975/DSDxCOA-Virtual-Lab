package coa.arithmetic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NonRestoringDivisionTest {

    @Test
    public void testPositiveDivision() {
        DivisionResult res = NonRestoringDivision.divide(13, 4, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testExactDivision() {
        DivisionResult res = NonRestoringDivision.divide(100, 10, 8);
        assertEquals(10, res.getQuotient());
        assertEquals(0, res.getRemainder());
    }

    @Test
    public void testNonExactDivision() {
        DivisionResult res = NonRestoringDivision.divide(7, 2, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testNegativeDividend() {
        DivisionResult res = NonRestoringDivision.divide(-13, 4, 8);
        assertEquals(-3, res.getQuotient());
        assertEquals(-1, res.getRemainder());
    }

    @Test
    public void testNegativeDivisor() {
        DivisionResult res = NonRestoringDivision.divide(13, -4, 8);
        assertEquals(-3, res.getQuotient());
        assertEquals(1, res.getRemainder());
    }

    @Test
    public void testBothNegative() {
        DivisionResult res = NonRestoringDivision.divide(-13, -4, 8);
        assertEquals(3, res.getQuotient());
        assertEquals(-1, res.getRemainder());
    }

    @Test
    public void testZeroDividend() {
        DivisionResult res = NonRestoringDivision.divide(0, 5, 8);
        assertEquals(0, res.getQuotient());
        assertEquals(0, res.getRemainder());
    }

    @Test
    public void testDivisionByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> NonRestoringDivision.divide(10, 0, 8));
    }

    @Test
    public void testAgreesWithRestoringDivision() {
        // Both algorithms should produce identical quotient/remainder for the same inputs.
        for (int dividend : new int[]{13, -13, 100, 7, 1, 0, 255}) {
            for (int divisor : new int[]{4, -4, 10, 2, 3, 1}) {
                DivisionResult restoring = RestoringDivision.divide(dividend, divisor, 8);
                DivisionResult nonRestoring = NonRestoringDivision.divide(dividend, divisor, 8);
                assertEquals(restoring.getQuotient(), nonRestoring.getQuotient(),
                    "Quotient mismatch for " + dividend + "/" + divisor);
                assertEquals(restoring.getRemainder(), nonRestoring.getRemainder(),
                    "Remainder mismatch for " + dividend + "/" + divisor);
            }
        }
    }
}
