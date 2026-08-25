package dsd.combinational;

import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.XorGate;
import java.util.HashMap;
import java.util.Map;

public class HalfAdder implements CombinationalCircuit {
    private final LogicGate xorGate = new XorGate();
    private final LogicGate andGate = new AndGate();

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean a = inputs.getOrDefault("A", false);
        boolean b = inputs.getOrDefault("B", false);

        boolean sum = xorGate.evaluate(a, b);
        boolean carry = andGate.evaluate(a, b);

        Map<String, Boolean> outputs = new HashMap<>();
        outputs.put("SUM", sum);
        outputs.put("CARRY", carry);
        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "Half Adder";
    }
}