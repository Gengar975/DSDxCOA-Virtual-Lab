package coa.cpu;

/** Accumulator: the register conventionally used to hold ALU operation results. */
public class Accumulator extends Register {

    public Accumulator(int width) {
        super("ACC", width);
    }
}
