package dsd.combinational;

import java.util.HashMap;
import java.util.Map;

import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.OrGate;
import dsd.gates.XorGate;

public class FullAdder implements CombinationalCircuit {
    private final LogicGate xorGate = new XorGate();
    private final LogicGate andGate = new AndGate();
    private final LogicGate orGate = new OrGate();

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean a = inputs.getOrDefault("A", false);
        boolean b = inputs.getOrDefault("B", false);
        boolean cin = inputs.getOrDefault("CIN", false);

        boolean axorb = xorGate.evaluate(a, b);
        boolean sum = xorGate.evaluate(axorb, cin);

        boolean ab = andGate.evaluate(a, b);
        boolean cinAxorb = andGate.evaluate(cin, axorb);
        boolean cout = orGate.evaluate(ab, cinAxorb);

        Map<String, Boolean> outputs = new HashMap<>();
        outputs.put("SUM", sum);
        outputs.put("COUT", cout);
        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "Full Adder";
    }
}