package dsd.sequential.registers;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ShiftRegisterTest {

    @Test
    public void testPipoLoadAndReadBack() {
        PipoShiftRegister reg = new PipoShiftRegister(4);
        reg.loadParallel(1, 0, 1, 1);
        assertEquals(List.of(true, false, true, true), reg.getParallelOutput());
    }

    @Test
    public void testPipoInvalidLengthThrows() {
        PipoShiftRegister reg = new PipoShiftRegister(4);
        assertThrows(IllegalArgumentException.class, () -> reg.loadParallel(1, 0, 1));
    }

    @Test
    public void testPipoReset() {
        PipoShiftRegister reg = new PipoShiftRegister(4);
        reg.loadParallel(1, 1, 1, 1);
        reg.reset();
        assertEquals(List.of(false, false, false, false), reg.getParallelOutput());
    }

    @Test
    public void testPisoLoadAndShiftOutMsbFirst() {
        PisoShiftRegister reg = new PisoShiftRegister(4);
        reg.loadParallel(1, 0, 1, 1); // 1011
        assertEquals(List.of(true, false, true, true), reg.getParallelState());

        int out1 = reg.clockPulse();
        assertEquals(1, out1); // MSB shifted out first
        assertEquals(List.of(false, true, true, false), reg.getParallelState()); // 0110
    }

    @Test
    public void testSipoShiftsInFromMsbSide() {
        SipoShiftRegister reg = new SipoShiftRegister(4);
        reg.clockPulse(1);
        reg.clockPulse(0);
        reg.clockPulse(1);
        reg.clockPulse(1);
        // After exactly bitWidth clocks, the serial stream is reconstructed
        // MSB-first in its original entry order: 1,0,1,1.
        assertEquals(List.of(true, false, true, true), reg.getParallelOutput());
    }

    @Test
    public void testSisoShiftsThroughCorrectly() {
        SisoShiftRegister reg = new SisoShiftRegister(3);
        assertEquals(0, reg.clockPulse(1)); // shift in 1, shift out initial 0
        assertEquals(0, reg.clockPulse(0)); // shift in 0, shift out 0
        assertEquals(0, reg.clockPulse(0)); // shift in 0, shift out 0
        assertEquals(1, reg.clockPulse(0)); // now the first '1' shifts out
    }

    @Test
    public void testInvalidBitWidthThrows() {
        assertThrows(IllegalArgumentException.class, () -> new SipoShiftRegister(0));
    }

    @Test
    public void testInvalidSerialInputThrows() {
        SipoShiftRegister reg = new SipoShiftRegister(4);
        assertThrows(IllegalArgumentException.class, () -> reg.clockPulse(5));
    }
}
