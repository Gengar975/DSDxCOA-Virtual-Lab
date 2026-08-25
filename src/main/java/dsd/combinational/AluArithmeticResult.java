package dsd.combinational;

public class AluArithmeticResult {
    private final boolean[] resultBits;
    private final String binaryString;
    private final boolean carryFlag;
    private final boolean zeroFlag;
    private final boolean signFlag;
    private final boolean overflowFlag;

    public AluArithmeticResult(boolean[] resultBits, boolean carryFlag, boolean overflowFlag) {
        this.resultBits = resultBits;
        this.carryFlag = carryFlag;
        this.overflowFlag = overflowFlag;

        StringBuilder sb = new StringBuilder();
        boolean allZero = true;
        for (boolean bit : resultBits) {
            sb.append(bit ? '1' : '0');
            if (bit) allZero = false;
        }

        this.binaryString = sb.toString();
        this.zeroFlag = allZero;
        this.signFlag = resultBits.length > 0 && resultBits[0]; // MSB indicates sign
    }

    public boolean[] getResultBits() { return resultBits; }
    public String getBinaryString() { return binaryString; }
    public boolean isCarryFlag() { return carryFlag; }
    public boolean isZeroFlag() { return zeroFlag; }
    public boolean isSignFlag() { return signFlag; }
    public boolean isOverflowFlag() { return overflowFlag; }

    @Override
    public String toString() {
        return "Result=" + binaryString + " [Z=" + (zeroFlag ? 1 : 0) +
               ", S=" + (signFlag ? 1 : 0) +
               ", C=" + (carryFlag ? 1 : 0) +
               ", V=" + (overflowFlag ? 1 : 0) + "]";
    }
}