package coa.alu;

import java.util.ArrayList;
import java.util.List;

import dsd.combinational.AluArithmeticResult;
import dsd.combinational.RippleCarryAdderSubtractor;
import dsd.gates.AndGate;
import dsd.gates.LogicGate;
import dsd.gates.NotGate;
import dsd.gates.OrGate;
import dsd.gates.XorGate;

/**
 * A simple ALU model built entirely from existing DSD primitives: addition
 * and subtraction are delegated to {@link RippleCarryAdderSubtractor}, and
 * bitwise logic operations are performed by applying the corresponding
 * {@code dsd.gates} primitive to each bit position.
 */
public class Alu {

    private final RippleCarryAdderSubtractor adderSubtractor = new RippleCarryAdderSubtractor();
    private final LogicGate andGate = new AndGate();
    private final LogicGate orGate = new OrGate();
    private final LogicGate xorGate = new XorGate();
    private final LogicGate notGate = new NotGate();

    /** Executes a binary (two-operand) ALU operation: ADD, SUB, AND, OR, or XOR. */
    public AluResult compute(boolean[] a, boolean[] b, AluOperation operation) {
        if (operation == AluOperation.NOT) {
            throw new IllegalArgumentException("NOT is a unary operation; use compute(boolean[], AluOperation) instead.");
        }
        if (a == null || b == null || a.length != b.length || a.length == 0) {
            throw new IllegalArgumentException("Operand bit arrays must be non-null, non-empty, and of equal length.");
        }

        List<String> steps = new ArrayList<>();
        String aBin = bitsToString(a);
        String bBin = bitsToString(b);
        steps.add("Operand A = " + aBin);
        steps.add("Operand B = " + bBin);
        steps.add("Operation = " + operation);

        switch (operation) {
            case ADD: {
                AluArithmeticResult r = adderSubtractor.compute(a, b, false);
                steps.add("A + B via RippleCarryAdderSubtractor -> " + r.getBinaryString());
                return new AluResult(aBin, bBin, operation, r.getBinaryString(),
                    r.isCarryFlag(), r.isZeroFlag(), r.isSignFlag(), r.isOverflowFlag(), steps);
            }
            case SUB: {
                AluArithmeticResult r = adderSubtractor.compute(a, b, true);
                steps.add("A - B via RippleCarryAdderSubtractor -> " + r.getBinaryString());
                return new AluResult(aBin, bBin, operation, r.getBinaryString(),
                    r.isCarryFlag(), r.isZeroFlag(), r.isSignFlag(), r.isOverflowFlag(), steps);
            }
            case AND:
                return bitwise(a, b, aBin, bBin, operation, andGate, steps);
            case OR:
                return bitwise(a, b, aBin, bBin, operation, orGate, steps);
            case XOR:
                return bitwise(a, b, aBin, bBin, operation, xorGate, steps);
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    /** Executes the unary NOT operation on a single operand. */
    public AluResult compute(boolean[] a, AluOperation operation) {
        if (operation != AluOperation.NOT) {
            throw new IllegalArgumentException("Only NOT is a unary operation.");
        }
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Operand bit array must be non-null and non-empty.");
        }

        List<String> steps = new ArrayList<>();
        String aBin = bitsToString(a);
        steps.add("Operand A = " + aBin);
        steps.add("Operation = NOT");

        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = notGate.evaluate(a[i]);
        }
        String resultBin = bitsToString(result);
        steps.add("NOT A (bitwise, via dsd.gates.NotGate) -> " + resultBin);

        boolean zero = resultBin.chars().allMatch(c -> c == '0');
        boolean sign = result[0];
        return new AluResult(aBin, null, operation, resultBin, false, zero, sign, false, steps);
    }

    private AluResult bitwise(boolean[] a, boolean[] b, String aBin, String bBin, AluOperation operation,
                               LogicGate gate, List<String> steps) {
        boolean[] result = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = gate.evaluate(a[i], b[i]);
        }
        String resultBin = bitsToString(result);
        steps.add("Bitwise " + operation + " (via dsd.gates " + gate.getName() + " gate, one instance per bit) -> " + resultBin);

        boolean zero = resultBin.chars().allMatch(c -> c == '0');
        boolean sign = result[0];
        return new AluResult(aBin, bBin, operation, resultBin, false, zero, sign, false, steps);
    }

    private static String bitsToString(boolean[] bits) {
        StringBuilder sb = new StringBuilder(bits.length);
        for (boolean bit : bits) {
            sb.append(bit ? '1' : '0');
        }
        return sb.toString();
    }
}
