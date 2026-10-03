package coa.memory;

import java.util.Collections;
import java.util.List;

/**
 * Immutable result of a single cache access, exposing the address's
 * tag/index/offset decomposition and whether the access was a HIT or MISS,
 * following the dsd project's result-object convention.
 */
public class CacheAccessResult {
    private final int address;
    private final int tag;
    private final int index;
    private final int offset;
    private final boolean hit;
    private final List<String> steps;

    public CacheAccessResult(int address, int tag, int index, int offset, boolean hit, List<String> steps) {
        this.address = address;
        this.tag = tag;
        this.index = index;
        this.offset = offset;
        this.hit = hit;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public int getAddress() { return address; }
    public int getTag() { return tag; }
    public int getIndex() { return index; }
    public int getOffset() { return offset; }
    public boolean isHit() { return hit; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return "Address=" + address + " [Tag=" + tag + ", Index=" + index + ", Offset=" + offset + "] -> " +
            (hit ? "HIT" : "MISS");
    }
}
