package dsd.sequential.components;

/**
 * Models a JK flip-flop: J=K=0 holds state, J=0/K=1 resets, J=1/K=0 sets,
 * and J=K=1 toggles.
 */
public class JKFlipFlop {

    private int q = 0;

    public int getQ() {
        return q;
    }

    public int getQBar() {
        return q == 0 ? 1 : 0;
    }

    public void clockPulse(int j, int k) {
        validateInput(j);
        validateInput(k);

        if (j == 0 && k == 0) {
            // No change
        } else if (j == 0 && k == 1) {
            q = 0;
        } else if (j == 1 && k == 0) {
            q = 1;
        } else {
            q = q == 0 ? 1 : 0;
        }
    }

    public void reset() {
        q = 0;
    }

    private void validateInput(int input) {
        if (input != 0 && input != 1) {
            throw new IllegalArgumentException("JK inputs must be either 0 or 1.");
        }
    }

    @Override
    public String toString() {
        return "JK Flip-Flop [Q=" + q + ", Q'=" + getQBar() + "]";
    }
}
