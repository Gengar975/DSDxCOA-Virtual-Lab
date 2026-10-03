package coa.cpu;

/** Instruction Register: holds the currently fetched instruction word. */
public class InstructionRegister extends Register {

    public InstructionRegister(int width) {
        super("IR", width);
    }
}
