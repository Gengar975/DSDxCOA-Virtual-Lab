package dsd.sequential.counters;

import java.util.ArrayList;
import java.util.List;

/** 4-bit BCD (mod-10) counter: counts 0 through 9 then rolls over. */
public class BcdCounter implements Counter {

    private static final int BIT_WIDTH = 4;
    private static final int MAX_BCD_VALUE = 9;

    private int value;

    public BcdCounter() {
        reset();
    }

    @Override
    public void clockTick() {
        if (value == MAX_BCD_VALUE) {
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
        return BIT_WIDTH;
    }

    @Override
    public CounterState getState() {

        List<Boolean> bits = new ArrayList<>();

        for (int i = BIT_WIDTH - 1; i >= 0; i--) {
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
        return "BCD Counter";
    }
}
