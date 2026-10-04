package dsd.sequential.components;

/** Models a D (Data) flip-flop: on each clock pulse, Q takes the value of D. */
public class DFlipFlop {

    private int q = 0;

    public int getQ() {
        return q;
    }

    public int getQBar() {
        return q == 0 ? 1 : 0;
    }

    public void clockPulse(int d) {
        if (d != 0 && d != 1) {
            throw new IllegalArgumentException("D input must be either 0 or 1.");
        }
        q = d;
    }

    public void reset() {
        q = 0;
    }

    @Override
    public String toString() {
        return "D Flip-Flop [Q=" + q + ", Q'=" + getQBar() + "]";
    }
}
