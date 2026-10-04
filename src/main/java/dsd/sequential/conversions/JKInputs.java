package dsd.sequential.conversions;

/**
 * Immutable J/K input pair produced by {@link FlipFlopConverter}, following
 * the project's result-object convention (private final fields + getters)
 * rather than a bare record.
 */
public class JKInputs {
    private final int j;
    private final int k;

    public JKInputs(int j, int k) {
        this.j = j;
        this.k = k;
    }

    public int getJ() { return j; }
    public int getK() { return k; }

    @Override
    public String toString() {
        return "J=" + j + ", K=" + k;
    }
}
