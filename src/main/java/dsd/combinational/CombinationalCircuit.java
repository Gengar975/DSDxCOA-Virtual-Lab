package dsd.combinational;

import java.util.Map;

public interface CombinationalCircuit {
    /**
     * Executes the combinational circuit logic.
     * @param inputs A map of input pin names to boolean values (e.g., "A" -> true, "B" -> false)
     * @return A map of output pin names to boolean values (e.g., "SUM" -> true, "CARRY" -> false)
     */
    Map<String, Boolean> compute(Map<String, Boolean> inputs);

    /**
     * @return Circuit component name (e.g., "Full Adder", "4:1 MUX")
     */
    String getCircuitName();
}