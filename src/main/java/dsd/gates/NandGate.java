package dsd.gates;

public class NandGate implements LogicGate {
    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length < 2) {
            throw new IllegalArgumentException("NAND gate requires at least 2 inputs.");
        }
        for (boolean in : inputs) {
            if (!in) return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "NAND";
    }
}