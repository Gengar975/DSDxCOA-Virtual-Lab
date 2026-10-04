package dsd.sequential.registers;

import java.util.ArrayList;
import java.util.List;

/** Parallel-In Parallel-Out shift register: loads and reads all bits at once. */
public class PipoShiftRegister implements ShiftRegister {

    private final int bitWidth;
    private final int[] bits;

    public PipoShiftRegister(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        this.bits = new int[bitWidth];
    }

    public void loadParallel(int... inputBits) {

        if (inputBits.length != bitWidth) {
            throw new IllegalArgumentException(
                    "Input length must match register bit width."
            );
        }

        for (int bit : inputBits) {
            validateBit(bit);
        }

        for (int i = 0; i < bitWidth; i++) {
            bits[i] = inputBits[i];
        }
    }

    public List<Boolean> getParallelOutput() {

        List<Boolean> output = new ArrayList<>();

        for (int bit : bits) {
            output.add(bit == 1);
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
        return bitWidth + "-bit PIPO Shift Register";
    }

    private void validateBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                    "Parallel inputs must be either 0 or 1."
            );
        }
    }
}
