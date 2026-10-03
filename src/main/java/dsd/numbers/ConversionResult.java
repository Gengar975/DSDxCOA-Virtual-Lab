package dsd.numbers;

import java.util.Collections;
import java.util.List;

public class ConversionResult {
    private final String originalValue;
    private final String convertedValue;
    private final List<String> steps;

    public ConversionResult(String originalValue, String convertedValue, List<String> steps) {
        this.originalValue = originalValue;
        this.convertedValue = convertedValue;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getConvertedValue() {
        return convertedValue;
    }

    public List<String> getSteps() {
        return steps;
    }
}