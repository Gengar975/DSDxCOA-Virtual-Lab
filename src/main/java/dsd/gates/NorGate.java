package dsd.gates;

public class NorGate implements LogicGate {
    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length < 2) {
            throw new IllegalArgumentException("NOR gate requires at least 2 inputs.");
        }
        for (boolean in : inputs) {
            if (in) return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "NOR";
    }
}