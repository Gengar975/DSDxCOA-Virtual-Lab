package dsd.sequential.registers;

import java.util.Collections;
import java.util.List;

/**
 * Immutable snapshot of a shift register's current bit pattern, following
 * the project's result-object convention rather than a bare record.
 */
public class ShiftRegisterState {
    private final List<Boolean> bits;

    public ShiftRegisterState(List<Boolean> bits) {
        this.bits = bits != null ? bits : Collections.emptyList();
    }

    public List<Boolean> getBits() { return bits; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean bit : bits) {
            sb.append(bit ? '1' : '0');
        }
        return sb.toString();
    }
}
