package playground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The playground model: components + connections + evaluation.
 * No JavaFX dependencies. The UI calls these methods and reads state back.
 */
public class Circuit {

    private final Map<String, PlaygroundComponent> components = new LinkedHashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private final Map<ComponentType, Integer> counters = new HashMap<>();
    private SimulationResult lastResult;

    // ---------- components ----------

    /** Adds a component with an auto-generated id (e.g. "AND_1") and returns that id. */
    public String addComponent(ComponentType type, double x, double y) {
        if (type == null) throw new IllegalArgumentException("Component type is required.");
        int n = counters.merge(type, 1, Integer::sum);
        String id = type.name() + "_" + n;
        components.put(id, new PlaygroundComponent(id, type, id, new Position(x, y)));
        lastResult = null;
        return id;
    }

    /** Removes a component and every wire attached to it. */
    public void removeComponent(String id) {
        get(id);
        components.remove(id);
        connections.removeIf(c -> c.sourceId().equals(id) || c.targetId().equals(id));
        lastResult = null;
    }

    public void moveComponent(String id, double x, double y) {
        get(id).setPosition(new Position(x, y));
    }

    public void setLabel(String id, String label) {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Label must not be blank.");
        get(id).setLabel(label);
    }

    public PlaygroundComponent getComponent(String id) {
        return get(id);
    }

    public List<PlaygroundComponent> getComponents() {
        return List.copyOf(components.values());
    }

    // ---------- connections ----------

    /** Wires the output of {@code sourceId} into input port {@code targetPort} of {@code targetId}. */
    public void connect(String sourceId, String targetId, int targetPort) {
        PlaygroundComponent source = get(sourceId);
        PlaygroundComponent target = get(targetId);
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("A component cannot be connected to itself.");
        }
        if (!source.getType().hasOutputPort()) {
            throw new IllegalArgumentException(sourceId + " has no output to connect from.");
        }
        int ports = target.getType().getInputPortCount();
        if (ports == 0) {
            throw new IllegalArgumentException(targetId + " has no input ports.");
        }
        if (targetPort < 0 || targetPort >= ports) {
            throw new IllegalArgumentException(targetId + " has no input port " + targetPort + ".");
        }
        for (Connection c : connections) {
            if (c.targetId().equals(targetId) && c.targetPort() == targetPort) {
                throw new IllegalArgumentException("Input port " + targetPort + " of " + targetId + " is already connected.");
            }
        }
        if (reaches(targetId, sourceId)) {
            throw new IllegalArgumentException("This connection would create a feedback loop (not supported).");
        }
        connections.add(new Connection(sourceId, targetId, targetPort));
        lastResult = null;
    }

    public void disconnect(String targetId, int targetPort) {
        get(targetId);
        if (!connections.removeIf(c -> c.targetId().equals(targetId) && c.targetPort() == targetPort)) {
            throw new IllegalArgumentException("No connection at port " + targetPort + " of " + targetId + ".");
        }
        lastResult = null;
    }

    public List<Connection> getConnections() {
        return List.copyOf(connections);
    }

    // ---------- inputs ----------

    public void setInputValue(String inputId, boolean value) {
        PlaygroundComponent c = get(inputId);
        if (c.getType() != ComponentType.INPUT) {
            throw new IllegalArgumentException(inputId + " is not an INPUT component.");
        }
        c.setInputValue(value);
        lastResult = null;
    }

    // ---------- validation / run / reset ----------

    /** Returns a list of problems that stop the circuit running (empty = OK). */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        boolean hasInput = false;
        boolean hasOutput = false;
        for (PlaygroundComponent c : components.values()) {
            if (c.getType() == ComponentType.INPUT) hasInput = true;
            if (c.getType() == ComponentType.OUTPUT) hasOutput = true;
            for (int p = 0; p < c.getType().getInputPortCount(); p++) {
                if (sourceOf(c.getId(), p) == null) {
                    problems.add(c.getLabel() + ": input port " + p + " is not connected.");
                }
            }
        }
        if (!hasInput) problems.add("Circuit has no INPUT component.");
        if (!hasOutput) problems.add("Circuit has no OUTPUT component.");
        return problems;
    }

    /** Evaluates the whole circuit. Throws IllegalStateException if validate() reports problems. */
    public SimulationResult run() {
        List<String> problems = validate();
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Circuit cannot run: " + String.join(" ", problems));
        }
        Map<String, Boolean> values = new HashMap<>();
        List<ComponentState> steps = new ArrayList<>();
        for (PlaygroundComponent c : evaluationOrder()) {
            List<Boolean> ins = new ArrayList<>();
            for (int p = 0; p < c.getType().getInputPortCount(); p++) {
                ins.add(values.get(sourceOf(c.getId(), p)));
            }
            boolean out;
            if (c.getType() == ComponentType.INPUT) {
                out = c.getInputValue();
            } else if (c.getType() == ComponentType.OUTPUT) {
                out = ins.get(0);
            } else {
                boolean[] arr = new boolean[ins.size()];
                for (int i = 0; i < arr.length; i++) arr[i] = ins.get(i);
                out = c.getType().getGate().evaluate(arr); // reuses dsd.gates
            }
            values.put(c.getId(), out);
            steps.add(new ComponentState(c.getId(), c.getType(), c.getLabel(), ins, out));
        }
        lastResult = new SimulationResult(steps);
        return lastResult;
    }

    /** Result of the last run, or null if the circuit changed (or was reset) since. */
    public SimulationResult getLastResult() {
        return lastResult;
    }

    /** Sets every INPUT back to 0 and clears results. Structure and positions are kept. */
    public void reset() {
        for (PlaygroundComponent c : components.values()) c.setInputValue(false);
        lastResult = null;
    }

    /** Removes everything. */
    public void clear() {
        components.clear();
        connections.clear();
        counters.clear();
        lastResult = null;
    }

    // ---------- internals ----------

    private PlaygroundComponent get(String id) {
        PlaygroundComponent c = id == null ? null : components.get(id);
        if (c == null) throw new IllegalArgumentException("Unknown component: " + id);
        return c;
    }

    private String sourceOf(String targetId, int port) {
        for (Connection c : connections) {
            if (c.targetId().equals(targetId) && c.targetPort() == port) return c.sourceId();
        }
        return null;
    }

    /** True if following wires forward from {@code from} can reach {@code to}. */
    private boolean reaches(String from, String to) {
        List<String> stack = new ArrayList<>(List.of(from));
        List<String> seen = new ArrayList<>();
        while (!stack.isEmpty()) {
            String cur = stack.remove(stack.size() - 1);
            if (cur.equals(to)) return true;
            if (seen.contains(cur)) continue;
            seen.add(cur);
            for (Connection c : connections) {
                if (c.sourceId().equals(cur)) stack.add(c.targetId());
            }
        }
        return false;
    }

    /** Kahn's algorithm: a component is evaluated after everything feeding it. */
    private List<PlaygroundComponent> evaluationOrder() {
        Map<String, Integer> waiting = new LinkedHashMap<>();
        for (PlaygroundComponent c : components.values()) {
            waiting.put(c.getId(), c.getType().getInputPortCount());
        }
        List<PlaygroundComponent> order = new ArrayList<>();
        List<String> ready = new ArrayList<>();
        waiting.forEach((id, n) -> { if (n == 0) ready.add(id); });
        while (!ready.isEmpty()) {
            String id = ready.remove(0);
            order.add(components.get(id));
            for (Connection c : connections) {
                if (c.sourceId().equals(id) && waiting.merge(c.targetId(), -1, Integer::sum) == 0) {
                    ready.add(c.targetId());
                }
            }
        }
        return order;
    }
}
