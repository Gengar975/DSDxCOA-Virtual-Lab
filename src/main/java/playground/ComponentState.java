package playground;

import java.util.List;

/**
 * What one component saw and produced during a run.
 * For OUTPUT, {@code outputValue} equals the signal it received.
 */
public record ComponentState(String componentId, ComponentType type, String label,
                             List<Boolean> inputValues, boolean outputValue) {

    /** Human-readable line for the "show what is happening inside" panel. */
    public String describe() {
        if (type == ComponentType.INPUT) {
            return label + " = " + bit(outputValue);
        }
        if (type == ComponentType.OUTPUT) {
            return label + " receives " + bit(outputValue);
        }
        StringBuilder sb = new StringBuilder(label).append(" receives ");
        for (int i = 0; i < inputValues.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(bit(inputValues.get(i)));
        }
        return sb.append(" -> produces ").append(bit(outputValue)).toString();
    }

    private static String bit(boolean b) {
        return b ? "1" : "0";
    }
}
