package dsd.sequential.counters;

import java.util.ArrayList;
import java.util.List;

/** Synchronous (parallel) binary counter, counting 0 through 2^bitWidth-1. */
public class SynchronousBinaryCounter implements Counter {

    private final int bitWidth;
    private final int maxValue;
    private int value;

    public SynchronousBinaryCounter(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        this.maxValue = (1 << bitWidth) - 1;

        reset();
    }

    @Override
    public void clockTick() {
        if (value == maxValue) {
            value = 0;
        } else {
            value++;
        }
    }

    @Override
    public void reset() {
        value = 0;
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
        return bitWidth + "-bit Synchronous Binary Counter";
    }
}
