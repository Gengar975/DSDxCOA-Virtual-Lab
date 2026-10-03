package coa.cpu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {

    @Test
    public void testInitialization() {
        Register r = new Register("R0", 8);
        assertEquals(0, r.read());
        assertEquals("00000000", r.toBinaryString());
    }

    @Test
    public void testWriteAndRead() {
        Register r = new Register("R0", 8);
        r.write(42);
        assertEquals(42, r.read());
    }

    @Test
    public void testWriteNegativeValue() {
        Register r = new Register("R0", 8);
        r.write(-1);
        assertEquals(-1, r.read());
        assertEquals("11111111", r.toBinaryString());
    }

    @Test
    public void testClear() {
        Register r = new Register("R0", 8);
        r.write(99);
        r.clear();
        assertEquals(0, r.read());
    }

    @Test
    public void testWidthValidationOnConstruction() {
        assertThrows(IllegalArgumentException.class, () -> new Register("R0", 0));
    }

    @Test
    public void testInvalidValueOutOfRange() {
        Register r = new Register("R0", 4); // signed range [-8, 7]
        assertThrows(IllegalArgumentException.class, () -> r.write(100));
    }

    @Test
    public void testProgramCounterIncrement() {
        ProgramCounter pc = new ProgramCounter(8);
        pc.increment();
        pc.increment();
        assertEquals(2, pc.read());
    }

    @Test
    public void testProgramCounterAdvance() {
        ProgramCounter pc = new ProgramCounter(8);
        pc.write(10);
        pc.advance(5);
        assertEquals(15, pc.read());
    }

    @Test
    public void testAccumulatorName() {
        Accumulator acc = new Accumulator(8);
        assertEquals("ACC", acc.getName());
    }

    @Test
    public void testInstructionRegisterStoresBits() {
        InstructionRegister ir = new InstructionRegister(8);
        ir.write(-1);
        assertEquals("11111111", ir.toBinaryString());
    }
}
