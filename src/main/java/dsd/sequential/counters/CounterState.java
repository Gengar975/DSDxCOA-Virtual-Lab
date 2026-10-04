package dsd.sequential.counters;

import java.util.Collections;
import java.util.List;

/**
 * Immutable snapshot of a counter's current value, exposing both the
 * decimal value and its bit pattern (MSB first), following the project's
 * result-object convention rather than a bare record.
 */
public class CounterState {
    private final int decimalValue;
    private final List<Boolean> bits;

    public CounterState(int decimalValue, List<Boolean> bits) {
        this.decimalValue = decimalValue;
        this.bits = bits != null ? bits : Collections.emptyList();
    }

    public int getDecimalValue() { return decimalValue; }
    public List<Boolean> getBits() { return bits; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean bit : bits) {
            sb.append(bit ? '1' : '0');
        }
        return sb + " (" + decimalValue + ")";
    }
}
