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
        if (bitWidth < 2) {
            throw new IllegalArgumentException("bitWidth must be at least 2.");
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

            if (q0 && !qMinus1) {
                // 10 -> A = A - M
                AluArithmeticResult sub = ADDER.compute(a, m, true);
                a = sub.getResultBits();
                step.append(" -> A = A - M -> A = ").append(BitUtils.toBinaryString(a));
            } else if (!q0 && qMinus1) {
                // 01 -> A = A + M
                AluArithmeticResult add = ADDER.compute(a, m, false);
                a = add.getResultBits();
                step.append(" -> A = A + M -> A = ").append(BitUtils.toBinaryString(a));
            } else {
                // 00 or 11 -> no arithmetic operation
                step.append(" -> no operation (A unchanged = ").append(BitUtils.toBinaryString(a)).append(")");
            }

            // Arithmetic right shift across the combined A:Q:Q-1 register.
            boolean newQMinus1 = q[bitWidth - 1];
            for (int i = bitWidth - 1; i >= 1; i--) {
                q[i] = q[i - 1];
            }
            q[0] = a[bitWidth - 1];
            boolean signBit = a[0]; // preserved by arithmetic shift
            for (int i = bitWidth - 1; i >= 1; i--) {
                a[i] = a[i - 1];
            }
            a[0] = signBit;
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
