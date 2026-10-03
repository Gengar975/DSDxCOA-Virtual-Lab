package coa.memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CacheTest {

    // 4 lines, 16 bytes/line -> offsetBits=4, indexBits=2.

    @Test
    public void testInitialMiss() {
        Cache cache = new Cache(4, 16);
        CacheAccessResult res = cache.access(0);
        assertFalse(res.isHit());
    }

    @Test
    public void testSubsequentHit() {
        Cache cache = new Cache(4, 16);
        cache.access(0);
        CacheAccessResult res = cache.access(0);
        assertTrue(res.isHit());
    }

    @Test
    public void testDifferentAddressSameLineMiss() {
        Cache cache = new Cache(4, 16);
        cache.access(0);                 // tag 0, index 0 -> miss, loads tag 0
        CacheAccessResult res = cache.access(64); // tag 1, index 0 -> miss (tag mismatch)
        assertFalse(res.isHit());
        assertEquals(1, res.getTag());
        assertEquals(0, res.getIndex());
    }

    @Test
    public void testTagIndexOffsetDecomposition() {
        Cache cache = new Cache(4, 16);
        // address 37 = 0b100101 -> offset = 5, index = 2, tag = 0
        CacheAccessResult res = cache.access(37);
        assertEquals(5, res.getOffset());
        assertEquals(2, res.getIndex());
        assertEquals(0, res.getTag());
    }

    @Test
    public void testBoundaryAddress() {
        Cache cache = new Cache(4, 16);
        // last address mapped to line 3 with tag 0: 4*16 - 1 = 63
        CacheAccessResult res = cache.access(63);
        assertEquals(3, res.getIndex());
        assertEquals(15, res.getOffset());
        assertEquals(0, res.getTag());
    }

    @Test
    public void testInvalidNegativeAddressThrows() {
        Cache cache = new Cache(4, 16);
        assertThrows(IllegalArgumentException.class, () -> cache.access(-1));
    }

    @Test
    public void testReplacementAfterMiss() {
        Cache cache = new Cache(4, 16);
        cache.access(0);   // tag 0, index 0 -> miss
        cache.access(64);  // tag 1, index 0 -> miss, replaces tag 0
        CacheAccessResult res = cache.access(64); // tag 1, index 0 -> now a hit
        assertTrue(res.isHit());
    }

    @Test
    public void testInvalidConstructorArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Cache(3, 16)); // not a power of two
    }
}
