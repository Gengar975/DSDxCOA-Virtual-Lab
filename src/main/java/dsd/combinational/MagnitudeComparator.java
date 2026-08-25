package dsd.combinational;

public class MagnitudeComparator {

    public static class Result {
        public final boolean aGreaterThanB;
        public final boolean aEqualsB;
        public final boolean aLessThanB;

        public Result(boolean gt, boolean eq, boolean lt) {
            this.aGreaterThanB = gt;
            this.aEqualsB = eq;
            this.aLessThanB = lt;
        }
    }

    public static Result compare4Bit(int a, int b) {
        if (a < 0 || a > 15 || b < 0 || b > 15) {
            throw new IllegalArgumentException("Inputs must be 4-bit unsigned integers (0 to 15).");
        }
        return new Result(a > b, a == b, a < b);
    }
}