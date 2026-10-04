package dsd.sequential.registers;

import java.util.ArrayList;
import java.util.List;

/** Parallel-In Serial-Out shift register: loads all bits at once, shifts out one bit per clock. */
public class PisoShiftRegister implements ShiftRegister {

    private final int bitWidth;
    private final int[] bits;

    public PisoShiftRegister(int bitWidth) {
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

        /*
         * Input is given in normal MSB -> LSB order.
         *
         * Example:
         * loadParallel(1, 0, 1, 1)
         *
         * represents:
         * 1011
         */
        for (int i = 0; i < bitWidth; i++) {
            bits[i] = inputBits[bitWidth - 1 - i];
        }
    }

    public int clockPulse() {

        /*
         * MSB-side stage is shifted out.
         */
        int serialOutput = bits[bitWidth - 1];

        for (int i = bitWidth - 1; i > 0; i--) {
            bits[i] = bits[i - 1];
        }

        bits[0] = 0;

        return serialOutput;
    }

    public List<Boolean> getParallelState() {

        List<Boolean> state = new ArrayList<>();

        for (int i = bitWidth - 1; i >= 0; i--) {
            state.add(bits[i] == 1);
        }

        return List.copyOf(state);
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
                getParallelState()
        );
    }

    @Override
    public String getName() {
        return bitWidth + "-bit PISO Shift Register";
    }

    private void validateBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                    "Parallel inputs must be either 0 or 1."
            );
        }
    }
}
