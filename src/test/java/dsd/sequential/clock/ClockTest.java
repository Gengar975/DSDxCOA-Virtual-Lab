package dsd.sequential.clock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClockTest {

    @Test
    public void testInitialPulseCountIsZero() {
        Clock clock = new Clock();
        assertEquals(0, clock.getPulseCount());
    }

    @Test
    public void testPulseIncrementsCount() {
        Clock clock = new Clock();
        clock.pulse();
        clock.pulse();
        clock.pulse();
        assertEquals(3, clock.getPulseCount());
    }

    @Test
    public void testReset() {
        Clock clock = new Clock();
        clock.pulse();
        clock.pulse();
        clock.reset();
        assertEquals(0, clock.getPulseCount());
    }
}
