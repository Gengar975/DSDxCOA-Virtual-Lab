package coa.memory;

/**
 * An educational, word-addressable memory model: fixed capacity, address
 * validation, and simple read/write. This is ordinary bookkeeping logic —
 * no DSD hardware primitive applies to a memory array.
 */
public class Memory {
    private final int capacity;
    private final long[] words;

    public Memory(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Memory capacity must be at least 1 word.");
        }
        this.capacity = capacity;
        this.words = new long[capacity];
    }

    /** Writes a value into the given address. */
    public void write(int address, long value) {
        validateAddress(address);
        words[address] = value;
    }

    /** Reads the value stored at the given address. */
    public long read(int address) {
        validateAddress(address);
        return words[address];
    }

    /** Number of addressable words in this memory. */
    public int getCapacity() {
        return capacity;
    }

    private void validateAddress(int address) {
        if (address < 0 || address >= capacity) {
            throw new IndexOutOfBoundsException(
                "Address " + address + " is out of bounds for memory of capacity " + capacity + ".");
        }
    }
}
