package dsd.combinational;

import dsd.gates.LogicGate;
import dsd.gates.XorGate;
import java.util.Map;

public class RippleCarryAdderSubtractor {
    private final FullAdder fullAdder = new FullAdder();
    private final LogicGate xorGate = new XorGate();

    /**
     * Executes N-bit Ripple Carry Addition or Subtraction.
     * @param aBits Array of boolean bits for operand A (MSB at index 0)
     * @param bBits Array of boolean bits for operand B (MSB at index 0)
     * @param subtractMode true for subtraction (A - B), false for addition (A + B)
     */
    public AluArithmeticResult compute(boolean[] aBits, boolean[] bBits, boolean subtractMode) {
        if (aBits == null || bBits == null || aBits.length != bBits.length || aBits.length == 0) {
            throw new IllegalArgumentException("Operand bit arrays must be non-null, non-empty, and of equal length.");
        }

        int n = aBits.length;
        boolean[] resultBits = new boolean[n];
        boolean carry = subtractMode; // Cin = 1 for subtraction, 0 for addition
        boolean carryInToMsb = false;

        // Process from LSB (index n-1) up to MSB (index 0)
        for (int i = n - 1; i >= 0; i--) {
            boolean a = aBits[i];
            boolean b = xorGate.evaluate(bBits[i], subtractMode); // Invert B if subtracting

            if (i == 0) {
                carryInToMsb = carry; // Capture carry into MSB for overflow detection
            }

            Map<String, Boolean> faOut = fullAdder.compute(Map.of("A", a, "B", b, "CIN", carry));
            resultBits[i] = faOut.get("SUM");
            carry = faOut.get("COUT");
        }

        // Signed Overflow: V = CarryOut_MSB XOR CarryIn_MSB
        boolean overflow = carry ^ carryInToMsb;

        return new AluArithmeticResult(resultBits, carry, overflow);
    }

    /**
     * Helper overload to process binary strings directly (e.g. "0101", "0011").
     */
    public AluArithmeticResult compute(String aBin, String bBin, boolean subtractMode) {
        if (aBin.length() != bBin.length()) {
            throw new IllegalArgumentException("Binary strings must have the same bit-length.");
        }
        boolean[] a = new boolean[aBin.length()];
        boolean[] b = new boolean[bBin.length()];

        for (int i = 0; i < aBin.length(); i++) {
            a[i] = aBin.charAt(i) == '1';
            b[i] = bBin.charAt(i) == '1';
        }

        return compute(a, b, subtractMode);
    }
}