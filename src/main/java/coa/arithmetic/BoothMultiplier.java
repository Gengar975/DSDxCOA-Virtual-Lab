package coa.arithmetic;

import java.util.ArrayList;
import java.util.List;

import dsd.combinational.AluArithmeticResult;
import dsd.combinational.RippleCarryAdderSubtractor;

/**
 * Implements signed Booth's multiplication algorithm.
 * <p>
 * The add/subtract step (A = A + M or A = A - M) is delegated to
 * {@link RippleCarryAdderSubtractor}, exactly as it is used elsewhere in the
 * DSD project, rather than using Java's {@code +}/{@code -} operators
 * directly. The arithmetic right shift of the combined A:Q:Q-1 register is
 * performed explicitly bit-by-bit to model the actual hardware shift.
 */
public final class BoothMultiplier {

    private static final RippleCarryAdderSubtractor ADDER = new RippleCarryAdderSubtractor();

    private BoothMultiplier() {
    }

    /** Multiplies two signed values using an 8-bit Booth's algorithm register width. */
    public static BoothMultiplicationResult multiply(long multiplicand, long multiplier) {
        return multiply(multiplicand, multiplier, 8);
    }

    /**
     * Multiplies two signed values using Booth's algorithm with the given register width.
     * @param bitWidth width (in bits) of the A, Q and M registers; both operands must fit
     *                 in this signed range.
     */
    public static BoothMultiplicationResult multiply(long multiplicand, long multiplier, int bitWidth) {
        if (bitWidth < 2 || bitWidth > 62) {
            throw new IllegalArgumentException("bitWidth must be between 2 and 62 (got " + bitWidth + ").");
        }
        BitUtils.requireFitsSigned(multiplicand, bitWidth, "Multiplicand");
        BitUtils.requireFitsSigned(multiplier, bitWidth, "Multiplier");

        List<String> steps = new ArrayList<>();

        boolean[] m = BitUtils.toBits(multiplicand, bitWidth);
        boolean[] a = new boolean[bitWidth];              // accumulator, starts at 0
        boolean[] q = BitUtils.toBits(multiplier, bitWidth);
        boolean qMinus1 = false;

        steps.add("Initial: M (multiplicand) = " + BitUtils.toBinaryString(m) + " (" + multiplicand + ")");
        steps.add("Initial: Q (multiplier)   = " + BitUtils.toBinaryString(q) + " (" + multiplier + ")");
        steps.add("Initial: A = " + BitUtils.toBinaryString(a) + ", Q-1 = 0");

        for (int iteration = 1; iteration <= bitWidth; iteration++) {
            boolean q0 = q[bitWidth - 1];
            StringBuilder step = new StringBuilder("Iteration " + iteration + ": Q0Q-1 = " +
                (q0 ? '1' : '0') + (qMinus1 ? '1' : '0'));

            // The add/subtract is done on (bitWidth + 1) bits. A + M or A - M can need one
            // more bit than the registers hold (e.g. 0 - (-8) = 8 does not fit in 4 bits),
            // so a sign-extended extra bit keeps the intermediate value correct.
            boolean[] aExt = BitUtils.padLeading(a, 1, true);
            boolean[] mExt = BitUtils.padLeading(m, 1, true);
            boolean[] sum;

            if (q0 && !qMinus1) {
                // 10 -> A = A - M
                sum = ADDER.compute(aExt, mExt, true).getResultBits();
                step.append(" -> A = A - M -> A = ").append(BitUtils.toBinaryString(sum));
            } else if (!q0 && qMinus1) {
                // 01 -> A = A + M
                sum = ADDER.compute(aExt, mExt, false).getResultBits();
                step.append(" -> A = A + M -> A = ").append(BitUtils.toBinaryString(sum));
            } else {
                // 00 or 11 -> no arithmetic operation
                sum = aExt;
                step.append(" -> no operation (A unchanged = ").append(BitUtils.toBinaryString(a)).append(")");
            }

            // Arithmetic right shift across the combined A:Q:Q-1 register. The last bit of
            // the (bitWidth + 1)-bit sum drops into the top of Q; the first bitWidth bits of
            // the sum become the new A (this is exactly "sum shifted right, sign kept").
            boolean newQMinus1 = q[bitWidth - 1];
            for (int i = bitWidth - 1; i >= 1; i--) {
                q[i] = q[i - 1];
            }
            q[0] = sum[bitWidth];
            a = java.util.Arrays.copyOfRange(sum, 0, bitWidth);
            qMinus1 = newQMinus1;

            step.append(" | after arithmetic shift: A = ").append(BitUtils.toBinaryString(a))
                .append(", Q = ").append(BitUtils.toBinaryString(q))
                .append(", Q-1 = ").append(qMinus1 ? '1' : '0');
            steps.add(step.toString());
        }

        boolean[] product = BitUtils.concat(a, q);
        long productValue = BitUtils.toSignedLong(product);
        String productBinary = BitUtils.toBinaryString(product);

        steps.add("Final product (A:Q) = " + productBinary + " = " + productValue);

        return new BoothMultiplicationResult(multiplicand, multiplier, productValue, productBinary, bitWidth, steps);
    }
}
