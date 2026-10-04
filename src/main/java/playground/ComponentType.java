package playground;

import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.NandGate;
import dsd.gates.NorGate;
import dsd.gates.NotGate;
import dsd.gates.OrGate;
import dsd.gates.XnorGate;
import dsd.gates.XorGate;

/**
 * The only component kinds the limited playground supports.
 * Gate behaviour is NOT re-implemented here: each gate type simply delegates
 * to the existing {@link LogicGate} classes in {@code dsd.gates}.
 */
public enum ComponentType {
    INPUT(0, null),
    OUTPUT(1, null),
    AND(2, new AndGate()),
    OR(2, new OrGate()),
    NOT(1, new NotGate()),
    NAND(2, new NandGate()),
    NOR(2, new NorGate()),
    XOR(2, new XorGate()),
    XNOR(2, new XnorGate());

    private final int inputPortCount;
    private final LogicGate gate;

    ComponentType(int inputPortCount, LogicGate gate) {
        this.inputPortCount = inputPortCount;
        this.gate = gate;
    }

    /** Number of input ports (INPUT has 0). */
    public int getInputPortCount() {
        return inputPortCount;
    }

    /** Every component except OUTPUT drives one output signal. */
    public boolean hasOutputPort() {
        return this != OUTPUT;
    }

    public boolean isGate() {
        return gate != null;
    }

    /** The existing DSD gate used for evaluation, or null for INPUT/OUTPUT. */
    public LogicGate getGate() {
        return gate;
    }
}
