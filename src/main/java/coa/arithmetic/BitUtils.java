package coa.arithmetic;

/**
 * Small conversion helpers shared across the {@code coa} package.
 * <p>
 * These are plain bookkeeping utilities (turning an {@code int} into a
 * two's-complement bit array, or a bit array back into a decimal value) —
 * not a hardware component in their own right. All actual bit-level
 * addition/subtraction is still delegated to
 * {@link dsd.combinational.RippleCarryAdderSubtractor}, and all bitwise
 * logic to the {@code dsd.gates} primitives, per the project convention.
 * <p>
 * Bit arrays follow the same convention used throughout {@code dsd}:
 * index 0 is the most-significant bit.
 */
public final class BitUtils {

    private BitUtils() {
    }

    /** Converts a signed value into an n-bit two's-complement bit array (MSB at index 0). */
    public static boolean[] toBits(long value, int width) {
        boolean[] bits = new boolean[width];
        for (int i = 0; i < width; i++) {
            int shift = width - 1 - i;
            bits[i] = ((value >> shift) & 1L) != 0;
        }
        return bits;
    }

    /** Interprets an n-bit array (MSB at index 0) as a signed two's-complement value. */
    public static long toSignedLong(boolean[] bits) {
        long value = 0;
        for (boolean bit : bits) {
            value = (value << 1) | (bit ? 1 : 0);
        }
        int width = bits.length;
        long signBitMask = 1L << (width - 1);
        if (width < 64 && (value & signBitMask) != 0) {
            value -= (1L << width);
        }
        return value;
    }

    /** Interprets an n-bit array (MSB at index 0) as an unsigned value. */
    public static long toUnsignedLong(boolean[] bits) {
        long value = 0;
        for (boolean bit : bits) {
            value = (value << 1) | (bit ? 1 : 0);
        }
        return value;
    }

    /** Renders a bit array as a "0101" style string. */
    public static String toBinaryString(boolean[] bits) {
        StringBuilder sb = new StringBuilder(bits.length);
        for (boolean bit : bits) {
            sb.append(bit ? '1' : '0');
        }
        return sb.toString();
    }

    /** Returns a copy of {@code bits} padded with {@code extraLeadingBits} zero/sign bits at the front (index 0). */
    public static boolean[] padLeading(boolean[] bits, int extraLeadingBits, boolean signExtend) {
        boolean[] result = new boolean[bits.length + extraLeadingBits];
        boolean signBit = signExtend && bits.length > 0 && bits[0];
        for (int i = 0; i < extraLeadingBits; i++) {
            result[i] = signBit;
        }
        System.arraycopy(bits, 0, result, extraLeadingBits, bits.length);
        return result;
    }

    /** Concatenates two bit arrays (MSB-first): {@code high} followed by {@code low}. */
    public static boolean[] concat(boolean[] high, boolean[] low) {
        boolean[] result = new boolean[high.length + low.length];
        System.arraycopy(high, 0, result, 0, high.length);
        System.arraycopy(low, 0, result, high.length, low.length);
        return result;
    }

    /** Validates that {@code value} fits in a signed two's-complement field of {@code width} bits. */
    public static void requireFitsSigned(long value, int width, String label) {
        long min = -(1L << (width - 1));
        long max = (1L << (width - 1)) - 1;
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                label + " (" + value + ") does not fit in a " + width + "-bit signed range [" + min + ", " + max + "].");
        }
    }
}
