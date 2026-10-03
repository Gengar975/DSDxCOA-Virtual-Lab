package coa.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple direct-mapped cache model: each address decomposes into
 * Tag | Index | Offset, and an access is a HIT when the addressed line is
 * valid and its stored tag matches; otherwise it is a MISS and the line is
 * loaded with the new tag (simple direct-mapped replacement — no LRU needed
 * since each index has exactly one line).
 * <p>
 * Tag/index/offset arithmetic is ordinary Java logic, as explicitly allowed
 * by the assignment for cache calculations.
 */
public class Cache {
    private final int numLines;
    private final int lineSizeBytes;
    private final int offsetBits;
    private final int indexBits;
    private final CacheLine[] lines;

    public Cache(int numLines, int lineSizeBytes) {
        if (!isPowerOfTwo(numLines) || !isPowerOfTwo(lineSizeBytes)) {
            throw new IllegalArgumentException("numLines and lineSizeBytes must both be powers of two.");
        }
        this.numLines = numLines;
        this.lineSizeBytes = lineSizeBytes;
        this.offsetBits = log2(lineSizeBytes);
        this.indexBits = log2(numLines);
        this.lines = new CacheLine[numLines];
        for (int i = 0; i < numLines; i++) {
            lines[i] = new CacheLine();
        }
    }

    /** Accesses the given byte address, returning its tag/index/offset decomposition and HIT/MISS outcome. */
    public CacheAccessResult access(int address) {
        if (address < 0) {
            throw new IllegalArgumentException("Address must be non-negative.");
        }

        int offset = address & ((1 << offsetBits) - 1);
        int index = (address >>> offsetBits) & ((1 << indexBits) - 1);
        int tag = address >>> (offsetBits + indexBits);

        List<String> steps = new ArrayList<>();
        steps.add("Address " + address + " decomposes into Tag=" + tag + ", Index=" + index + ", Offset=" + offset);

        CacheLine line = lines[index];
        boolean hit = line.valid && line.tag == tag;

        if (hit) {
            steps.add("Line " + index + " is valid with matching tag " + tag + " -> HIT");
        } else if (line.valid) {
            steps.add("Line " + index + " is valid but holds tag " + line.tag + " (mismatch) -> MISS, replacing with tag " + tag);
            line.tag = tag;
        } else {
            steps.add("Line " + index + " is empty (invalid) -> MISS, loading tag " + tag);
            line.tag = tag;
            line.valid = true;
        }

        return new CacheAccessResult(address, tag, index, offset, hit, steps);
    }

    public int getNumLines() { return numLines; }
    public int getLineSizeBytes() { return lineSizeBytes; }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private static int log2(int n) {
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    private static class CacheLine {
        boolean valid = false;
        int tag = -1;
    }
}
