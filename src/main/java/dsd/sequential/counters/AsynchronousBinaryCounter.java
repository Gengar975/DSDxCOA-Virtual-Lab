package dsd.sequential.counters;

import dsd.sequential.components.TFlipFlop;

import java.util.ArrayList;
import java.util.List;

/** Ripple (asynchronous) binary counter built from a chain of T flip-flops. */
public class AsynchronousBinaryCounter implements Counter {

    private final int bitWidth;
    private TFlipFlop[] flipFlops;

    public AsynchronousBinaryCounter(int bitWidth) {
        if (bitWidth <= 0) {
            throw new IllegalArgumentException(
                    "Bit width must be greater than zero."
            );
        }

        this.bitWidth = bitWidth;
        reset();
    }

    @Override
    public void clockTick() {

        /*
         * First flip-flop receives the external clock.
         * T = 1, therefore it toggles.
         */
        int previousQ = flipFlops[0].getQ();

        flipFlops[0].clockPulse(1);

        /*
         * In an asynchronous/ripple counter, the next flip-flop
         * toggles when the previous flip-flop changes from 1 to 0.
         */
        for (int i = 1; i < bitWidth; i++) {

            int currentQ = flipFlops[i - 1].getQ();

            if (previousQ == 1 && currentQ == 0) {

                previousQ = flipFlops[i].getQ();

                flipFlops[i].clockPulse(1);

            } else {
                break;
            }
        }
    }

    @Override
    public void reset() {

        flipFlops = new TFlipFlop[bitWidth];

        for (int i = 0; i < bitWidth; i++) {
            flipFlops[i] = new TFlipFlop();
        }
    }

    @Override
    public int getValue() {

        int value = 0;

        for (int i = 0; i < bitWidth; i++) {

            if (flipFlops[i].getQ() == 1) {
                value |= (1 << i);
            }
        }

        return value;
    }

    @Override
    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public CounterState getState() {

        List<Boolean> bits = new ArrayList<>();

        /*
         * Return MSB first so a 3-bit value appears as:
         * 010 instead of 010 being stored in reverse stage order.
         */
        for (int i = bitWidth - 1; i >= 0; i--) {
            bits.add(flipFlops[i].getQ() == 1);
        }

        return new CounterState(
                getValue(),
                List.copyOf(bits)
        );
    }

    @Override
    public String getName() {
        return bitWidth + "-bit Asynchronous Binary Counter";
    }
}
