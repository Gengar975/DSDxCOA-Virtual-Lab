package coa.arithmetic;

import java.util.ArrayList;
import java.util.List;

import dsd.combinational.AluArithmeticResult;
import dsd.combinational.RippleCarryAdderSubtractor;

/**
 * Implements the classic non-restoring division algorithm on unsigned
 * magnitudes, with the sign of dividend/divisor applied to the final
 * quotient/remainder (truncating division, matching Java's {@code /} and
 * {@code %} semantics).
 * <p>
 * Unlike restoring division, each iteration performs exactly one add or
 * subtract (no separate restore step); a single correction add is applied
 * at the end if the final remainder is negative. All add/subtract steps
 * are delegated to {@link RippleCarryAdderSubtractor}.
 */
public final class NonRestoringDivision {

    private static final RippleCarryAdderSubtractor ADDER = new RippleCarryAdderSubtractor();

    private NonRestoringDivision() {
    }

    /** Divides two signed values using an 8-bit non-restoring division register width. */
    public static DivisionResult divide(long dividend, long divisor) {
        return divide(dividend, divisor, 8);
    }

    /**
     * Divides two signed values using non-restoring division with the given register width.
     * @param bitWidth width (in bits) of the Q (quotient) register; both operand
     *                 magnitudes must fit in this unsigned range.
     */
    public static DivisionResult divide(long dividend, long divisor, int bitWidth) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero.");
        }
        if (bitWidth < 2) {
            throw new IllegalArgumentException("bitWidth must be at least 2.");
        }

        List<String> steps = new ArrayList<>();

        boolean signQuotient = (dividend < 0) ^ (divisor < 0);
        boolean signRemainder = dividend < 0;
        long absDividend = Math.abs(dividend);
        long absDivisor = Math.abs(divisor);

        long maxUnsigned = (1L << bitWidth) - 1;
        if (absDividend > maxUnsigned || absDivisor > maxUnsigned) {
            throw new IllegalArgumentException(
                "Operand magnitude exceeds " + bitWidth + "-bit register width.");
        }

        boolean[] a = new boolean[bitWidth + 1];
        boolean[] q = BitUtils.toBits(absDividend, bitWidth);
        boolean[] m = BitUtils.padLeading(BitUtils.toBits(absDivisor, bitWidth), 1, false);

        steps.add("Initial: |Dividend| Q = " + BitUtils.toBinaryString(q) + " (" + absDividend + ")");
        steps.add("Initial: |Divisor|  M = " + BitUtils.toBinaryString(m) + " (" + absDivisor + ")");
        steps.add("Initial: A = " + BitUtils.toBinaryString(a));

        for (int iteration = 1; iteration <= bitWidth; iteration++) {
            // Left shift the combined A:Q register by 1 bit.
            for (int i = 0; i < a.length - 1; i++) {
                a[i] = a[i + 1];
            }
            a[a.length - 1] = q[0];
            for (int i = 0; i < q.length - 1; i++) {
                q[i] = q[i + 1];
            }
            q[q.length - 1] = false;

            StringBuilder step = new StringBuilder("Iteration " + iteration +
                ": after shift, A = " + BitUtils.toBinaryString(a) + ", Q = " + BitUtils.toBinaryString(q));

            boolean aWasNegative = a[0];
            AluArithmeticResult opResult = ADDER.compute(a, m, !aWasNegative);
            a = opResult.getResultBits();
            boolean nowNegative = a[0];
            q[q.length - 1] = !nowNegative;

            step.append(aWasNegative ? " | A was negative -> A = A + M -> " : " | A was non-negative -> A = A - M -> ")
                .append(BitUtils.toBinaryString(a))
                .append(" -> Q0 = ").append(nowNegative ? 0 : 1);
            steps.add(step.toString());
        }

        if (a[0]) {
            AluArithmeticResult corrected = ADDER.compute(a, m, false);
            a = corrected.getResultBits();
            steps.add("Final correction: A negative -> A = A + M -> " + BitUtils.toBinaryString(a));
        } else {
            steps.add("Final correction: A already non-negative, no correction needed.");
        }

        long quotientMagnitude = BitUtils.toUnsignedLong(q);
        long remainderMagnitude = BitUtils.toUnsignedLong(a);

        long quotient = signQuotient ? -quotientMagnitude : quotientMagnitude;
        long remainder = remainderMagnitude == 0 ? 0 : (signRemainder ? -remainderMagnitude : remainderMagnitude);

        steps.add("Final: Quotient magnitude = " + quotientMagnitude + ", Remainder magnitude = " + remainderMagnitude);
        steps.add("Final (signed): Quotient = " + quotient + ", Remainder = " + remainder);

        return new DivisionResult(dividend, divisor, quotient, remainder, "Non-Restoring Division", steps);
    }
}
