package dsd.gates;

public class NotGate implements LogicGate {
    @Override
    public boolean evaluate(boolean... inputs) {
        if (inputs == null || inputs.length != 1) {
            throw new IllegalArgumentException("NOT gate requires exactly 1 input.");
        }
        return !inputs[0];
    }

    @Override
    public String getName() {
        return "NOT";
    }
}