package playground;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of evaluating a circuit. {@code steps} is in signal-flow order
 * (inputs first), so the UI can reveal them one at a time for a step view.
 */
public record SimulationResult(List<ComponentState> steps) {

    public SimulationResult {
        steps = List.copyOf(steps);
    }

    /** Signal on the output wire of the given component (or received by an OUTPUT). */
    public boolean getValue(String componentId) {
        return getState(componentId).outputValue();
    }

    public ComponentState getState(String componentId) {
        for (ComponentState s : steps) {
            if (s.componentId().equals(componentId)) return s;
        }
        throw new IllegalArgumentException("No such component in result: " + componentId);
    }

    /** OUTPUT component id -> final value. */
    public Map<String, Boolean> getOutputs() {
        Map<String, Boolean> out = new LinkedHashMap<>();
        for (ComponentState s : steps) {
            if (s.type() == ComponentType.OUTPUT) out.put(s.componentId(), s.outputValue());
        }
        return out;
    }
}
