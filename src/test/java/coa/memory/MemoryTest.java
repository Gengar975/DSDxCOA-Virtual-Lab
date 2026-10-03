package coa.memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemoryTest {

    @Test
    public void testValidWriteAndRead() {
        Memory memory = new Memory(16);
        memory.write(0, 123);
        assertEquals(123, memory.read(0));
    }

    @Test
    public void testBoundaryAddresses() {
        Memory memory = new Memory(16);
        memory.write(0, 1);
        memory.write(15, 2);
        assertEquals(1, memory.read(0));
        assertEquals(2, memory.read(15));
    }

    @Test
    public void testInvalidAddressNegative() {
        Memory memory = new Memory(16);
        assertThrows(IndexOutOfBoundsException.class, () -> memory.read(-1));
    }

    @Test
    public void testInvalidAddressTooLarge() {
        Memory memory = new Memory(16);
        assertThrows(IndexOutOfBoundsException.class, () -> memory.write(16, 5));
    }

    @Test
    public void testCapacity() {
        Memory memory = new Memory(32);
        assertEquals(32, memory.getCapacity());
    }
}
