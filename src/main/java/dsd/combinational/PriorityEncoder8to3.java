package dsd.combinational;

import java.util.HashMap;
import java.util.Map;

public class PriorityEncoder8to3 implements CombinationalCircuit {

    @Override
    public Map<String, Boolean> compute(Map<String, Boolean> inputs) {
        int highestActive = -1;

        for (int i = 7; i >= 0; i--) {
            if (Boolean.TRUE.equals(inputs.get("D" + i))) {
                highestActive = i;
                break;
            }
        }

        Map<String, Boolean> outputs = new HashMap<>();
        if (highestActive == -1) {
            outputs.put("A2", false);
            outputs.put("A1", false);
            outputs.put("A0", false);
            outputs.put("VALID", false);
        } else {
            outputs.put("A2", (highestActive & 4) != 0);
            outputs.put("A1", (highestActive & 2) != 0);
            outputs.put("A0", (highestActive & 1) != 0);
            outputs.put("VALID", true);
        }

        return outputs;
    }

    @Override
    public String getCircuitName() {
        return "8:3 Priority Encoder";
    }
}