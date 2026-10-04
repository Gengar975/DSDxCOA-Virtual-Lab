package dsd.sequential.counters;

import java.util.ArrayList;
import java.util.List;

/** Binary counter that can count up or down, wrapping at either boundary. */
public class UpDownCounter implements Counter {

    private final int bitWidth;
    private final int maxValue;
    private int value;
    private boolean countUp = true;

    public UpDownCounter(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        this.maxValue = (1 << bitWidth) - 1;

        reset();
    }

    public void setCountUp(boolean countUp) {
        this.countUp = countUp;
    }

    public boolean isCountUp() {
        return countUp;
    }

    @Override
    public void clockTick() {
        if (countUp) {
            if (value == maxValue) {
                value = 0;
            } else {
                value++;
            }
        } else {
            if (value == 0) {
                value = maxValue;
            } else {
                value--;
            }
        }
    }

    @Override
    public void reset() {
        value = 0;
        countUp = true;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public CounterState getState() {
        List<Boolean> bits = new ArrayList<>();

        for (int i = bitWidth - 1; i >= 0; i--) {
            boolean bit = ((value >> i) & 1) == 1;
            bits.add(bit);
        }

        return new CounterState(
                value,
                List.copyOf(bits)
        );
    }

    @Override
    public String getName() {
        return bitWidth + "-bit Up/Down Counter";
    }
}
