package dsd.gates;

public class AndGate implements LogicGate {

    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length < 2) {
            throw new IllegalArgumentException("AND gate requires at least 2 inputs.");
        }
        for (boolean in : inputs) {
            if (!in) {
                return false; // If any input is 0 (false), output is 0
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "AND";
    }
}