package dsd.combinational;

import java.util.HashMap;
import java.util.Map;

public class Multiplexer4to1 implements CombinationalCircuit {

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean i0 = inputs.getOrDefault("I0", false);
        boolean i1 = inputs.getOrDefault("I1", false);
        boolean i2 = inputs.getOrDefault("I2", false);
        boolean i3 = inputs.getOrDefault("I3", false);

        boolean s1 = inputs.getOrDefault("S1", false);
        boolean s0 = inputs.getOrDefault("S0", false);

        int select = (s1 ? 2 : 0) + (s0 ? 1 : 0);
        boolean out;

        switch (select) {
            case 0 -> out = i0;
            case 1 -> out = i1;
            case 2 -> out = i2;
            case 3 -> out = i3;
            default -> out = false;
        }

        Map<String, Boolean> outputs = new HashMap<>();
        outputs.put("Y", out);
        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "4:1 Multiplexer";
    }
}