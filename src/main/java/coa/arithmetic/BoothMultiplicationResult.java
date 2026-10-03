package coa.arithmetic;

import java.util.Collections;
import java.util.List;

/**
 * Immutable result of running signed Booth's multiplication algorithm,
 * following the same convention as {@code dsd.combinational.AluArithmeticResult}
 * and {@code dsd.numbers.ConversionResult}: final values plus an educational
 * step-by-step trace.
 */
public class BoothMultiplicationResult {
    private final long multiplicand;
    private final long multiplier;
    private final long product;
    private final String productBinary;
    private final int bitWidth;
    private final List<String> steps;

    public BoothMultiplicationResult(long multiplicand, long multiplier, long product,
                                      String productBinary, int bitWidth, List<String> steps) {
        this.multiplicand = multiplicand;
        this.multiplier = multiplier;
        this.product = product;
        this.productBinary = productBinary;
        this.bitWidth = bitWidth;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public long getMultiplicand() { return multiplicand; }
    public long getMultiplier() { return multiplier; }
    public long getProduct() { return product; }
    public String getProductBinary() { return productBinary; }
    public int getBitWidth() { return bitWidth; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return multiplicand + " x " + multiplier + " = " + product + " (" + productBinary + ")";
    }
}
