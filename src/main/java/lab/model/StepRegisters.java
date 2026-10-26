package lab.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pulls register contents (A = 0001, Q = 1010, M = 00011 ...) out of the engine's step text so the
 * UI can show the registers changing from step to step.
 */
public final class StepRegisters {

    private static final Pattern REG = Pattern.compile("(Q-1|[AMQ])(?:\\s*\\([^)]*\\))?\\s*=\\s*([01]+)");

    private StepRegisters() { }

    /** Registers mentioned in one step line (name to bit string), in order of appearance. */
    public static Map<String, String> parse(String step) {
        Map<String, String> out = new LinkedHashMap<>();
        Matcher m = REG.matcher(step);
        while (m.find()) out.put(m.group(1), m.group(2));
        return out;
    }
}
