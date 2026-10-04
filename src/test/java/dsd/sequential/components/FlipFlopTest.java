package dsd.sequential.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlipFlopTest {

    // --- D Flip-Flop ---

    @Test
    public void testDFlipFlopFollowsInput() {
        DFlipFlop ff = new DFlipFlop();
        ff.clockPulse(1);
        assertEquals(1, ff.getQ());
        assertEquals(0, ff.getQBar());
        ff.clockPulse(0);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testDFlipFlopInvalidInputThrows() {
        DFlipFlop ff = new DFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockPulse(2));
    }

    @Test
    public void testDFlipFlopReset() {
        DFlipFlop ff = new DFlipFlop();
        ff.clockPulse(1);
        ff.reset();
        assertEquals(0, ff.getQ());
    }

    // --- JK Flip-Flop ---

    @Test
    public void testJKFlipFlopHold() {
        JKFlipFlop ff = new JKFlipFlop();
        ff.clockPulse(1, 0); // set
        ff.clockPulse(0, 0); // hold
        assertEquals(1, ff.getQ());
    }

    @Test
    public void testJKFlipFlopResetState() {
        JKFlipFlop ff = new JKFlipFlop();
        ff.clockPulse(1, 0);
        ff.clockPulse(0, 1);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testJKFlipFlopToggle() {
        JKFlipFlop ff = new JKFlipFlop();
        ff.clockPulse(1, 1);
        assertEquals(1, ff.getQ());
        ff.clockPulse(1, 1);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testJKFlipFlopInvalidInputThrows() {
        JKFlipFlop ff = new JKFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockPulse(2, 0));
    }

    // --- SR Flip-Flop ---

    @Test
    public void testSRFlipFlopSetAndReset() {
        SRFlipFlop ff = new SRFlipFlop();
        ff.clockPulse(1, 0);
        assertEquals(1, ff.getQ());
        ff.clockPulse(0, 1);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testSRFlipFlopForbiddenStateThrows() {
        SRFlipFlop ff = new SRFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockPulse(1, 1));
    }

    @Test
    public void testSRFlipFlopInvalidBitThrows() {
        SRFlipFlop ff = new SRFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockPulse(3, 0));
    }

    // --- T Flip-Flop ---

    @Test
    public void testTFlipFlopToggle() {
        TFlipFlop ff = new TFlipFlop();
        ff.clockPulse(1);
        assertEquals(1, ff.getQ());
        ff.clockPulse(1);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testTFlipFlopHold() {
        TFlipFlop ff = new TFlipFlop();
        ff.clockPulse(0);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testTFlipFlopInvalidInputThrows() {
        TFlipFlop ff = new TFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockPulse(5));
    }

    // --- Master-Slave JK Flip-Flop ---

    @Test
    public void testMasterSlaveJKFlipFlopSetDelayedUntilClockLow() {
        MasterSlaveJKFlipFlop ff = new MasterSlaveJKFlipFlop();
        ff.clockHigh(1, 0);
        assertEquals(1, ff.getMasterQ());
        assertEquals(0, ff.getQ()); // slave hasn't updated yet
        ff.clockLow();
        assertEquals(1, ff.getQ());
    }

    @Test
    public void testMasterSlaveJKFlipFlopFullPulseToggles() {
        MasterSlaveJKFlipFlop ff = new MasterSlaveJKFlipFlop();
        ff.clockPulse(1, 1);
        assertEquals(1, ff.getQ());
        ff.clockPulse(1, 1);
        assertEquals(0, ff.getQ());
    }

    @Test
    public void testMasterSlaveJKFlipFlopInvalidInputThrows() {
        MasterSlaveJKFlipFlop ff = new MasterSlaveJKFlipFlop();
        assertThrows(IllegalArgumentException.class, () -> ff.clockHigh(2, 0));
    }

    @Test
    public void testMasterSlaveJKFlipFlopReset() {
        MasterSlaveJKFlipFlop ff = new MasterSlaveJKFlipFlop();
        ff.clockPulse(1, 0);
        ff.reset();
        assertEquals(0, ff.getQ());
        assertEquals(0, ff.getMasterQ());
    }
}
