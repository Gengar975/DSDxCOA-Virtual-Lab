package dsd.gates;

public class XorGate implements LogicGate {
    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length < 2) {
            throw new IllegalArgumentException("XOR gate requires at least 2 inputs.");
        }
        boolean result = false;
        for (boolean in : inputs) {
            result ^= in;
        }
        return result;
    }

    @Override
    public String getName() {
        return "XOR";
    }
}