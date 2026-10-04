package dsd.sequential.counters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CounterTest {

    @Test
    public void testAsynchronousBinaryCounterCountsUp() {
        AsynchronousBinaryCounter counter = new AsynchronousBinaryCounter(3);
        counter.clockTick();
        assertEquals(1, counter.getValue());
        counter.clockTick();
        assertEquals(2, counter.getValue());
        counter.clockTick();
        assertEquals(3, counter.getValue());
    }

    @Test
    public void testAsynchronousBinaryCounterRolloverAndState() {
        AsynchronousBinaryCounter counter = new AsynchronousBinaryCounter(2);
        counter.clockTick(); // 1
        counter.clockTick(); // 2
        counter.clockTick(); // 3
        counter.clockTick(); // rolls over to 0
        assertEquals(0, counter.getValue());
        assertEquals(0, counter.getState().getDecimalValue());
        assertFalse(counter.getState().getBits().get(0));
        assertFalse(counter.getState().getBits().get(1));
    }

    @Test
    public void testAsynchronousBinaryCounterInvalidWidthThrows() {
        assertThrows(IllegalArgumentException.class, () -> new AsynchronousBinaryCounter(0));
    }

    @Test
    public void testSynchronousBinaryCounterCountsAndWraps() {
        SynchronousBinaryCounter counter = new SynchronousBinaryCounter(2);
        counter.clockTick();
        counter.clockTick();
        counter.clockTick();
        assertEquals(3, counter.getValue());
        counter.clockTick();
        assertEquals(0, counter.getValue());
    }

    @Test
    public void testBcdCounterWrapsAtNine() {
        BcdCounter counter = new BcdCounter();
        for (int i = 0; i < 9; i++) {
            counter.clockTick();
        }
        assertEquals(9, counter.getValue());
        counter.clockTick();
        assertEquals(0, counter.getValue());
    }

    @Test
    public void testUpDownCounterCountsDown() {
        UpDownCounter counter = new UpDownCounter(3);
        counter.setCountUp(false);
        counter.clockTick();
        assertEquals(7, counter.getValue()); // wraps to max
        counter.clockTick();
        assertEquals(6, counter.getValue());
    }

    @Test
    public void testUpDownCounterResetRestoresCountUp() {
        UpDownCounter counter = new UpDownCounter(3);
        counter.setCountUp(false);
        counter.reset();
        assertTrue(counter.isCountUp());
        assertEquals(0, counter.getValue());
    }

    @Test
    public void testCounterStateBitOrderIsMsbFirst() {
        SynchronousBinaryCounter counter = new SynchronousBinaryCounter(4);
        counter.clockTick();
        counter.clockTick();
        counter.clockTick(); // value = 3 = 0011
        CounterState state = counter.getState();
        assertEquals(3, state.getDecimalValue());
        assertEquals(4, state.getBits().size());
        assertFalse(state.getBits().get(0)); // MSB
        assertTrue(state.getBits().get(3));  // LSB
    }
}
