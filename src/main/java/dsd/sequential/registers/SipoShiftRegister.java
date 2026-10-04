package dsd.sequential.registers;

import java.util.ArrayList;
import java.util.List;

/** Serial-In Parallel-Out shift register: shifts in one bit per clock, reads all bits at once. */
public class SipoShiftRegister implements ShiftRegister {

    private final int bitWidth;
    private final int[] bits;

    public SipoShiftRegister(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        this.bits = new int[bitWidth];
    }

    public void clockPulse(int serialInput) {

        validateBit(serialInput);

        for (int i = bitWidth - 1; i > 0; i--) {
            bits[i] = bits[i - 1];
        }

        bits[0] = serialInput;
    }

    public List<Boolean> getParallelOutput() {

        List<Boolean> output = new ArrayList<>();

        for (int i = bitWidth - 1; i >= 0; i--) {
            output.add(bits[i] == 1);
        }

        return List.copyOf(output);
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
        return new ShiftRegisterState(
                getParallelOutput()
        );
    }

    @Override
    public String getName() {
        return bitWidth + "-bit SIPO Shift Register";
    }

    private void validateBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                    "Serial input must be either 0 or 1."
            );
        }
    }
}
