package dsd.gates;

public interface LogicGate {
    /**
     * Evaluates the logic gate output given variable inputs.
     * @param inputs Array or varargs of boolean inputs (true = 1, false = 0)
     * @return boolean output of the gate
     */
    boolean evaluate(boolean... inputs);

    /**
     * @return The standard name of the gate (e.g., "AND", "NAND")
     */
    String getName();
}