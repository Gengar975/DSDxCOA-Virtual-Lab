package dsd.sequential.components;

/**
 * Models an SR (Set-Reset) flip-flop. S=1,R=1 is the flip-flop's forbidden
 * state (both set and reset asserted at once) and is rejected rather than
 * silently producing an undefined output.
 */
public class SRFlipFlop {

    private int q = 0;

    public int getQ() {
        return q;
    }

    public int getQBar() {
        return q == 0 ? 1 : 0;
    }

    public void clockPulse(int s, int r) {
        validateInput(s);
        validateInput(r);

        if (s == 0 && r == 0) {
            // No change
        } else if (s == 0 && r == 1) {
            q = 0;
        } else if (s == 1 && r == 0) {
            q = 1;
        } else {
            throw new IllegalArgumentException(
                "S=1 and R=1 is a forbidden state for an SR flip-flop (undefined output).");
        }
    }

    public void reset() {
        q = 0;
    }

    private void validateInput(int input) {
        if (input != 0 && input != 1) {
            throw new IllegalArgumentException("SR inputs must be either 0 or 1.");
        }
    }

    @Override
    public String toString() {
        return "SR Flip-Flop [Q=" + q + ", Q'=" + getQBar() + "]";
    }
}
