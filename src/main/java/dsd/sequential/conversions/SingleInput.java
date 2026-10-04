package dsd.sequential.conversions;

/**
 * Immutable single-bit input produced by {@link FlipFlopConverter}, following
 * the project's result-object convention (private final fields + getters)
 * rather than a bare record.
 */
public class SingleInput {
    private final int value;

    public SingleInput(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
