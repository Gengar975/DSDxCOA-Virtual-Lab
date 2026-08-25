package dsd.gates;

public class OrGate implements LogicGate {
    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length < 2) {
            throw new IllegalArgumentException("OR gate requires at least 2 inputs.");
        }
        for (boolean in : inputs) {
            if (in) return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "OR";
    }
}