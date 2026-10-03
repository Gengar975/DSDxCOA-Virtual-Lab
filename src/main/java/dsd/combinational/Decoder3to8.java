package dsd.combinational;

import java.util.HashMap;
import java.util.Map;

public class Decoder3to8 implements CombinationalCircuit {

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        boolean enable = inputs.getOrDefault("E", true);
        boolean a2 = inputs.getOrDefault("A2", false);
        boolean a1 = inputs.getOrDefault("A1", false);
        boolean a0 = inputs.getOrDefault("A0", false);

        Map<String, Boolean> outputs = new HashMap<>();

        for (int i = 0; i < 8; i++) {
            outputs.put("Y" + i, false);
        }

        if (enable) {
            int selectedIndex = (a2 ? 4 : 0) + (a1 ? 2 : 0) + (a0 ? 1 : 0);
            outputs.put("Y" + selectedIndex, true);
        }

        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "3:8 Decoder";
    }
}