package dsd.combinational;

import java.util.HashMap;
import java.util.Map;

import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.NotGate;
import dsd.gates.OrGate;
import dsd.gates.XorGate;

public class FullSubtractor implements CombinationalCircuit {
    private final LogicGate xorGate = new XorGate();
    private final LogicGate andGate = new AndGate();
    private final LogicGate orGate = new OrGate();
    private final LogicGate notGate = new NotGate();

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean a = inputs.getOrDefault("A", false);
        boolean b = inputs.getOrDefault("B", false);
        boolean bin = inputs.getOrDefault("BIN", false);

        boolean axorb = xorGate.evaluate(a, b);
        boolean diff = xorGate.evaluate(axorb, bin);

        boolean notA = notGate.evaluate(a);
        boolean notA_and_B = andGate.evaluate(notA, b);

        boolean not_axorb = notGate.evaluate(axorb);
        boolean term2 = andGate.evaluate(not_axorb, bin);

        boolean bout = orGate.evaluate(notA_and_B, term2);

        Map<String, Boolean> outputs = new HashMap<>();
        outputs.put("DIFF", diff);
        outputs.put("BOUT", bout);
        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "Full Subtractor";
    }
}