package coa.cpu;

import coa.arithmetic.BitUtils;

/**
 * A simple general-purpose CPU register: fixed bit width, holds a signed
 * two's-complement value, and supports read/write/clear. Bit storage and
 * width validation are ordinary bookkeeping (no DSD hardware primitive
 * applies here), per the assignment's guidance.
 */
public class Register {
    private final String name;
    private final int width;
    private boolean[] bits;

    public Register(String name, int width) {
        if (width < 1) {
            throw new IllegalArgumentException("Register width must be at least 1 bit.");
        }
        this.name = name;
        this.width = width;
        this.bits = new boolean[width];
    }

    /** Loads a signed value into the register; must fit within the register's width. */
    public void write(long value) {
        BitUtils.requireFitsSigned(value, width, "Value for register '" + name + "'");
        this.bits = BitUtils.toBits(value, width);
    }

    /** Directly loads a bit pattern (MSB at index 0); array length must equal the register width. */
    public void writeBits(boolean[] newBits) {
        if (newBits == null || newBits.length != width) {
            throw new IllegalArgumentException(
                "Bit pattern must be exactly " + width + " bits for register '" + name + "'.");
        }
        this.bits = newBits.clone();
    }

    /** Reads the register's current value as a signed two's-complement number. */
    public long read() {
        return BitUtils.toSignedLong(bits);
    }

    /** Reads the register's current bit pattern (a defensive copy). */
    public boolean[] readBits() {
        return bits.clone();
    }

    /** Resets the register to all-zero. */
    public void clear() {
        this.bits = new boolean[width];
    }

    public String getName() { return name; }
    public int getWidth() { return width; }
    public String toBinaryString() { return BitUtils.toBinaryString(bits); }

    @Override
    public String toString() {
        return name + " = " + toBinaryString() + " (" + read() + ")";
    }
}
