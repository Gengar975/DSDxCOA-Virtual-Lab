package coa.cpu;

/**
 * Program Counter: a register that additionally supports incrementing to
 * the next instruction address.
 */
public class ProgramCounter extends Register {

    public ProgramCounter(int width) {
        super("PC", width);
    }

    /** Advances the program counter by one address. */
    public void increment() {
        write(read() + 1);
    }

    /** Advances the program counter by the given offset (e.g. for a branch). */
    public void advance(long offset) {
        write(read() + offset);
    }
}
