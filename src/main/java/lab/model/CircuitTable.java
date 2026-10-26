package lab.model;

import java.util.ArrayList;
import java.util.List;
import playground.Circuit;
import playground.ComponentType;
import playground.PlaygroundComponent;

/**
 * The complete truth table of a {@link Circuit}: every combination of its INPUT switches is
 * run through the real simulation. Used by the lesson tables and by the playground.
 */
public final class CircuitTable {

    private final List<String> inputIds = new ArrayList<>();
    private final List<String> outputIds = new ArrayList<>();
    private final List<String> inputLabels = new ArrayList<>();
    private final List<String> outputLabels = new ArrayList<>();
    private final List<boolean[]> inputRows = new ArrayList<>();
    private final List<boolean[]> outputRows = new ArrayList<>();

    /** Maximum number of INPUT switches (2^n rows). */
    public static final int MAX_INPUTS = 6;

    public CircuitTable(Circuit circuit) {
        for (PlaygroundComponent c : circuit.getComponents()) {
            if (c.getType() == ComponentType.INPUT) { inputIds.add(c.getId()); inputLabels.add(c.getLabel()); }
            if (c.getType() == ComponentType.OUTPUT) { outputIds.add(c.getId()); outputLabels.add(c.getLabel()); }
        }
        int n = inputIds.size();
        if (n == 0 || outputIds.isEmpty()) throw new IllegalArgumentException("The circuit needs at least one INPUT and one OUTPUT.");
        if (n > MAX_INPUTS) throw new IllegalArgumentException("A table is shown for at most " + MAX_INPUTS + " inputs (this circuit has " + n + ").");
        boolean[] saved = new boolean[n];
        for (int i = 0; i < n; i++) saved[i] = circuit.getComponent(inputIds.get(i)).getInputValue();
        try {
            for (int row = 0; row < (1 << n); row++) {
                boolean[] in = new boolean[n];
                for (int i = 0; i < n; i++) {
                    in[i] = ((row >> (n - 1 - i)) & 1) == 1;   // first input is the most significant bit
                    circuit.setInputValue(inputIds.get(i), in[i]);
                }
                var result = circuit.run();
                boolean[] out = new boolean[outputIds.size()];
                for (int o = 0; o < out.length; o++) out[o] = result.getValue(outputIds.get(o));
                inputRows.add(in);
                outputRows.add(out);
            }
        } finally {
            for (int i = 0; i < n; i++) circuit.setInputValue(inputIds.get(i), saved[i]);
        }
    }

    public List<String> getInputLabels() { return List.copyOf(inputLabels); }
    public List<String> getOutputLabels() { return List.copyOf(outputLabels); }
    public int rowCount() { return inputRows.size(); }
    public boolean[] inputs(int row) { return inputRows.get(row).clone(); }
    public boolean[] outputs(int row) { return outputRows.get(row).clone(); }

    /** Which row matches the circuit's current switch positions. */
    public int rowFor(Circuit circuit) {
        int row = 0;
        for (String id : inputIds) row = (row << 1) | (circuit.getComponent(id).getInputValue() ? 1 : 0);
        return row;
    }
}
