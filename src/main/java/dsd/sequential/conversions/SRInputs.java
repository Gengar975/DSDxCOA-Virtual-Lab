package dsd.sequential.conversions;

/**
 * Immutable S/R input pair produced by {@link FlipFlopConverter}, following
 * the project's result-object convention (private final fields + getters)
 * rather than a bare record.
 */
public class SRInputs {
    private final int s;
    private final int r;

    public SRInputs(int s, int r) {
        this.s = s;
        this.r = r;
    }

    public int getS() { return s; }
    public int getR() { return r; }

    @Override
    public String toString() {
        return "S=" + s + ", R=" + r;
    }
}
