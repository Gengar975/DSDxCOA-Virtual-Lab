package dsd.sequential.components;

/** Models a T (Toggle) flip-flop: T=0 holds state, T=1 toggles Q. */
public class TFlipFlop {

    private int q = 0;

    public int getQ() {
        return q;
    }

    public int getQBar() {
        return q == 0 ? 1 : 0;
    }

    public void clockPulse(int t) {
        if (t != 0 && t != 1) {
            throw new IllegalArgumentException("T input must be either 0 or 1.");
        }
        if (t == 1) {
            q = q == 0 ? 1 : 0;
        }
        // t == 0 -> no change
    }

    public void reset() {
        q = 0;
    }

    @Override
    public String toString() {
        return "T Flip-Flop [Q=" + q + ", Q'=" + getQBar() + "]";
    }
}
