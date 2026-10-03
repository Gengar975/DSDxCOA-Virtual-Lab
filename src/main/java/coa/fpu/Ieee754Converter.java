package coa.fpu;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts values to and from IEEE 754 single-precision (32-bit) format.
 * <p>
 * There is no DSD hardware primitive for floating-point bit-field
 * extraction/construction, so — as explicitly allowed by the assignment —
 * this uses ordinary Java logic (bit shifting/masking) for the field work,
 * while relying on {@link Float#floatToIntBits(float)} /
 * {@link Float#intBitsToFloat(int)} as the authoritative IEEE 754 rounding
 * and bit-layout implementation (matching what real hardware/JVMs do), and
 * builds an educational step trace around it.
 */
public final class Ieee754Converter {

    private static final int MANTISSA_BITS = 23;
    private static final int EXPONENT_BITS = 8;
    private static final int BIAS = 127;

    private Ieee754Converter() {
    }

    /** Converts a decimal value into IEEE 754 single-precision representation. */
    public static Ieee754Result toIeee754(double value) {
        List<String> steps = new ArrayList<>();
        float fValue = (float) value;
        int bits = Float.floatToIntBits(fValue);
        return decodeBits(bits, value, steps, true);
    }

    /** Decodes an existing 32-bit IEEE 754 pattern back into its value and fields. */
    public static Ieee754Result fromIeee754(int bitPattern) {
        List<String> steps = new ArrayList<>();
        // Note: unlike toIeee754, we decode the raw bits directly (not via
        // floatToIntBits) so an exact, non-canonicalized NaN payload is preserved.
        float value = Float.intBitsToFloat(bitPattern);
        steps.add("Decoding raw 32-bit pattern " + toBinaryString(bitPattern, 32) +
            String.format(" (0x%08X)", bitPattern));
        return decodeBits(bitPattern, value, steps, false);
    }

    private static Ieee754Result decodeBits(int bits, double originalValue, List<String> steps, boolean forwardDirection) {
        int sign = (bits >>> 31) & 1;
        int biasedExponent = (bits >>> (MANTISSA_BITS)) & 0xFF;
        int mantissa = bits & 0x7FFFFF;

        String exponentBits = toBinaryString(biasedExponent, EXPONENT_BITS);
        String mantissaBits = toBinaryString(mantissa, MANTISSA_BITS);
        String binaryString = sign + exponentBits + mantissaBits;
        String hexString = String.format("0x%08X", bits);

        if (forwardDirection) {
            steps.add("Original value = " + originalValue + " (rounded to float: " + Float.intBitsToFloat(bits) + ")");
        }
        steps.add("Sign bit S = " + sign + " (" + (sign == 1 ? "negative" : "non-negative") + ")");

        String category;
        int actualExponent;
        float floatValue = Float.intBitsToFloat(bits);

        if (Float.isNaN(floatValue)) {
            category = "NAN";
            actualExponent = 0;
            steps.add("Biased exponent = " + biasedExponent + " (all 1s) with nonzero mantissa -> NaN");
        } else if (Float.isInfinite(floatValue)) {
            category = sign == 1 ? "NEGATIVE_INFINITY" : "POSITIVE_INFINITY";
            actualExponent = 0;
            steps.add("Biased exponent = " + biasedExponent + " (all 1s) with zero mantissa -> " +
                (sign == 1 ? "-Infinity" : "+Infinity"));
        } else if (biasedExponent == 0 && mantissa == 0) {
            category = sign == 1 ? "NEGATIVE_ZERO" : "ZERO";
            actualExponent = 0;
            steps.add("Biased exponent = 0 and mantissa = 0 -> " + (sign == 1 ? "-0" : "+0"));
        } else if (biasedExponent == 0) {
            category = "SUBNORMAL";
            actualExponent = 1 - BIAS;
            steps.add("Biased exponent = 0 with nonzero mantissa -> subnormal, implied leading bit = 0");
            steps.add("Actual exponent = 1 - bias = 1 - " + BIAS + " = " + actualExponent);
        } else {
            category = "NORMAL";
            actualExponent = biasedExponent - BIAS;
            steps.add("Biased exponent E = " + biasedExponent + " (" + exponentBits + ")");
            steps.add("Actual exponent = E - bias = " + biasedExponent + " - " + BIAS + " = " + actualExponent);
            steps.add("Mantissa/fraction (implied leading 1.) = 1." + mantissaBits);
        }

        steps.add("Final 32-bit pattern (S|Exponent|Mantissa) = " + sign + "|" + exponentBits + "|" + mantissaBits);
        steps.add("Hex representation = " + hexString);
        if (!forwardDirection) {
            steps.add("Decoded value = " + floatValue);
        }

        double reportedValue = forwardDirection ? originalValue : floatValue;
        return new Ieee754Result(reportedValue, sign, actualExponent, BIAS, exponentBits, mantissaBits,
            bits, binaryString, hexString, category, steps);
    }

    private static String toBinaryString(int value, int width) {
        String raw = Integer.toBinaryString(value);
        if (raw.length() > width) {
            raw = raw.substring(raw.length() - width);
        }
        return String.format("%" + width + "s", raw).replace(' ', '0');
    }
}
