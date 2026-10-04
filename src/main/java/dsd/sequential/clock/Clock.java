package dsd.sequential.clock;

/** Models a simple free-running clock pulse counter used to drive sequential components. */
public class Clock {

    private int pulseCount = 0;

    public void pulse() {
        pulseCount++;
    }

    public int getPulseCount() {
        return pulseCount;
    }

    public void reset() {
        pulseCount = 0;
    }
}
