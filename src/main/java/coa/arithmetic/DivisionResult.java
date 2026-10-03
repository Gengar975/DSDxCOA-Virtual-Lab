package coa.arithmetic;

import java.util.Collections;
import java.util.List;

/**
 * Immutable result of running a division algorithm (restoring or
 * non-restoring), following the same convention as
 * {@code dsd.combinational.AluArithmeticResult} and
 * {@code dsd.numbers.ConversionResult}.
 */
public class DivisionResult {
    private final long dividend;
    private final long divisor;
    private final long quotient;
    private final long remainder;
    private final String algorithm;
    private final List<String> steps;

    public DivisionResult(long dividend, long divisor, long quotient, long remainder,
                           String algorithm, List<String> steps) {
        this.dividend = dividend;
        this.divisor = divisor;
        this.quotient = quotient;
        this.remainder = remainder;
        this.algorithm = algorithm;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public long getDividend() { return dividend; }
    public long getDivisor() { return divisor; }
    public long getQuotient() { return quotient; }
    public long getRemainder() { return remainder; }
    public String getAlgorithm() { return algorithm; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return dividend + " / " + divisor + " = " + quotient + " remainder " + remainder + " (" + algorithm + ")";
    }
}
