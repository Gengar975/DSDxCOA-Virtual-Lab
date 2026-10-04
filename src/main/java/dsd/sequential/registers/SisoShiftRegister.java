package dsd.sequential.registers;

import java.util.ArrayList;
import java.util.List;

/** Serial-In Serial-Out shift register: shifts one bit in and one bit out per clock. */
public class SisoShiftRegister implements ShiftRegister {

    private final int bitWidth;
    private final int[] bits;

    public SisoShiftRegister(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        this.bits = new int[bitWidth];
    }

    public int clockPulse(int serialInput) {

        validateBit(serialInput);

        int serialOutput = bits[bitWidth - 1];

        for (int i = bitWidth - 1; i > 0; i--) {
            bits[i] = bits[i - 1];
        }

        bits[0] = serialInput;

        return serialOutput;
    }

    @Override
    public void reset() {
        for (int i = 0; i < bitWidth; i++) {
            bits[i] = 0;
        }
    }

    @Override
    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public ShiftRegisterState getState() {

        List<Boolean> state = new ArrayList<>();

        for (int i = bitWidth - 1; i >= 0; i--) {
            state.add(bits[i] == 1);
        }

        return new ShiftRegisterState(
                List.copyOf(state)
        );
    }

    @Override
    public String getName() {
        return bitWidth + "-bit SISO Shift Register";
    }

    private void validateBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                    "Serial input must be either 0 or 1."
            );
        }
    }
}
