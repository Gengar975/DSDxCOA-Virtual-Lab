package coa.alu;

import java.util.Collections;
import java.util.List;

/**
 * Immutable result of an ALU operation, following the same convention as
 * {@code dsd.combinational.AluArithmeticResult}: final values plus flags
 * plus an educational step trace. Flags that are not meaningful for a given
 * operation (e.g. carry/overflow for a bitwise AND) are reported as false.
 */
public class AluResult {
    private final String operandA;
    private final String operandB;
    private final AluOperation operation;
    private final String resultBinary;
    private final boolean carryFlag;
    private final boolean zeroFlag;
    private final boolean signFlag;
    private final boolean overflowFlag;
    private final List<String> steps;

    public AluResult(String operandA, String operandB, AluOperation operation, String resultBinary,
                      boolean carryFlag, boolean zeroFlag, boolean signFlag, boolean overflowFlag,
                      List<String> steps) {
        this.operandA = operandA;
        this.operandB = operandB;
        this.operation = operation;
        this.resultBinary = resultBinary;
        this.carryFlag = carryFlag;
        this.zeroFlag = zeroFlag;
        this.signFlag = signFlag;
        this.overflowFlag = overflowFlag;
        this.steps = steps != null ? steps : Collections.emptyList();
    }

    public String getOperandA() { return operandA; }
    public String getOperandB() { return operandB; }
    public AluOperation getOperation() { return operation; }
    public String getResultBinary() { return resultBinary; }
    public boolean isCarryFlag() { return carryFlag; }
    public boolean isZeroFlag() { return zeroFlag; }
    public boolean isSignFlag() { return signFlag; }
    public boolean isOverflowFlag() { return overflowFlag; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return operation + "(" + operandA + (operandB != null ? ", " + operandB : "") + ") = " + resultBinary +
            " [Z=" + (zeroFlag ? 1 : 0) + ", S=" + (signFlag ? 1 : 0) +
            ", C=" + (carryFlag ? 1 : 0) + ", V=" + (overflowFlag ? 1 : 0) + "]";
    }
}
