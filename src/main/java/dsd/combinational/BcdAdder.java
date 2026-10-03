package dsd.combinational;

public class BcdAdder {

    public static class BcdResult {
        public final int rawSum;
        public final int correctedBcdSum;
        public final boolean carryOut;
        public final boolean correctionApplied;

        public BcdResult(int rawSum, int correctedBcdSum, boolean carryOut, boolean correctionApplied) {
            this.rawSum = rawSum;
            this.correctedBcdSum = correctedBcdSum;
            this.carryOut = carryOut;
            this.correctionApplied = correctionApplied;
        }
    }

    /**
     * Adds two single-digit BCD numbers (0-9) with optional carry-in.
     */
    public static BcdResult add(int digitA, int digitB, boolean carryIn) {
        if (digitA < 0 || digitA > 9 || digitB < 0 || digitB > 9) {
            throw new IllegalArgumentException("BCD digits must be between 0 and 9.");
        }

        int rawSum = digitA + digitB + (carryIn ? 1 : 0);
        boolean needsCorrection = rawSum > 9;
        int finalSum = rawSum;
        boolean carryOut = false;

        if (needsCorrection) {
            finalSum = (rawSum + 6) % 16;
            carryOut = true;
        }

        return new BcdResult(rawSum, finalSum, carryOut, needsCorrection);
    }
}