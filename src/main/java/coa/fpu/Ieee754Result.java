package coa.fpu;

import java.util.Collections;
import java.util.List;

/**
 * Immutable result of converting a value to/from IEEE 754 single-precision
 * (32-bit) representation, following the dsd project's result-object
 * convention: private final fields, a steps trace, and a readable toString().
 */
public class Ieee754Result {
    private final double originalValue;
    private final int sign;
    private final int rawExponent;
    private final int biasedExponentBias;
    private final String exponentBits;
    private final String mantissaBits;
    private final int bitPattern;
    private final String binaryString;
    private final String hexString;
    private final String category;
    private final List<String> steps;

    public Ieee754Result(double originalValue, int sign, int rawExponent, int biasedExponentBias,
                          String exponentBits, String mantissaBits, int bitPattern,
                          String binaryString, String hexString, String category, List<String> steps) {
        this.originalValue = originalValue;
        this.sign = sign;
        this.rawExponent = rawExponent;
        this.biasedExponentBias = biasedExponentBias;
        this.exponentBits = exponentBits;
        this.mantissaBits = mantissaBits;
        this.bitPattern = bitPattern;
        this.binaryString = binaryString;
        this.hexString = hexString;
        this.category = category;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public double getOriginalValue() { return originalValue; }
    public int getSign() { return sign; }
    public int getRawExponent() { return rawExponent; }
    public int getBiasedExponentBias() { return biasedExponentBias; }
    public String getExponentBits() { return exponentBits; }
    public String getMantissaBits() { return mantissaBits; }
    public int getBitPattern() { return bitPattern; }
    public String getBinaryString() { return binaryString; }
    public String getHexString() { return hexString; }
    public String getCategory() { return category; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return originalValue + " -> " + binaryString + " (" + hexString + ") [" + category + "]";
    }
}
