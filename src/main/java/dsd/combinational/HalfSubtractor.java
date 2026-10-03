package dsd.combinational;

import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.NotGate;
import dsd.gates.XorGate;
import java.util.HashMap;
import java.util.Map;

public class HalfSubtractor implements CombinationalCircuit {
    private final LogicGate xorGate = new XorGate();
    private final LogicGate andGate = new AndGate();
    private final LogicGate notGate = new NotGate();

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean a = inputs.getOrDefault("A", false);
        boolean b = inputs.getOrDefault("B", false);

        boolean diff = xorGate.evaluate(a, b);
        boolean notA = notGate.evaluate(a);
        boolean borrow = andGate.evaluate(notA, b);

        Map<String, Boolean> outputs = new HashMap<>();
        outputs.put("DIFF", diff);
        outputs.put("BORROW", borrow);
        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "Half Subtractor";
    }
}