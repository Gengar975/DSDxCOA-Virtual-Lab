package dsd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import dsd.booleanlogic.KMapSolver;
import dsd.booleanlogic.TruthTable;
import dsd.combinational.AluArithmeticResult;
import dsd.combinational.BcdAdder;
import dsd.combinational.Decoder3to8;
import dsd.combinational.FullAdder;
import dsd.combinational.FullSubtractor;
import dsd.combinational.HalfAdder;
import dsd.combinational.HalfSubtractor;
import dsd.combinational.MagnitudeComparator;
import dsd.combinational.Multiplexer4to1;
import dsd.combinational.PriorityEncoder8to3;
import dsd.combinational.RippleCarryAdderSubtractor;
import dsd.numbers.ConversionResult;
import dsd.numbers.NumberConverter;

public class DsdService {

    // --- Number System Conversions ---

    public ConversionResult convertDecimalToBinary(long decimal) {
        return NumberConverter.decimalToBinary(decimal);
    }

    public ConversionResult convertBinaryToDecimal(String binary) {
        return NumberConverter.binaryToDecimal(binary);
    }

    public ConversionResult convertBinaryToGray(String binary) {
        return NumberConverter.binaryToGray(binary);
    }

    public ConversionResult convertDecimalToBcd(String decimalStr) {
        return NumberConverter.decimalToBcd(decimalStr);
    }

    public ConversionResult convertDecimalToExcess3(String decimalStr) {
        return NumberConverter.decimalToExcess3(decimalStr);
    }

    public ConversionResult convertDecimalToOctal(long decimal) {
        return NumberConverter.decimalToOctal(decimal);
    }

    public ConversionResult convertDecimalToHexadecimal(long decimal) {
        return NumberConverter.decimalToHexadecimal(decimal);
    }

    public ConversionResult convertOctalToDecimal(String octal) {
        return NumberConverter.octalToDecimal(octal);
    }

    public ConversionResult convertHexadecimalToDecimal(String hex) {
        return NumberConverter.hexadecimalToDecimal(hex);
    }

    public ConversionResult convertTextToAsciiBinary(String text) {
        return NumberConverter.textToAsciiBinary(text);
    }
    // --- Basic Combinational Adders / Subtractors ---

    public Map<String, Boolean> computeHalfAdder(boolean a, boolean b) {
        return new HalfAdder().compute(Map.of("A", a, "B", b));
    }

    public Map<String, Boolean> computeFullAdder(boolean a, boolean b, boolean cin) {
        return new FullAdder().compute(Map.of("A", a, "B", b, "CIN", cin));
    }

    public Map<String, Boolean> computeHalfSubtractor(boolean a, boolean b) {
        return new HalfSubtractor().compute(Map.of("A", a, "B", b));
    }

    public Map<String, Boolean> computeFullSubtractor(boolean a, boolean b, boolean bin) {
        return new FullSubtractor().compute(Map.of("A", a, "B", b, "BIN", bin));
    }

    public BcdAdder.BcdResult computeBcdAddition(int digitA, int digitB, boolean carryIn) {
        return BcdAdder.add(digitA, digitB, carryIn);
    }

    // --- Multi-Bit Arithmetic & ALU Engine ---

    public AluArithmeticResult computeRippleCarryArithmetic(String aBin, String bBin, boolean subtractMode) {
        return new RippleCarryAdderSubtractor().compute(aBin, bBin, subtractMode);
    }

    public AluArithmeticResult computeRippleCarryArithmetic(boolean[] aBits, boolean[] bBits, boolean subtractMode) {
        return new RippleCarryAdderSubtractor().compute(aBits, bBits, subtractMode);
    }

    // --- Selectors, Encoders & Decoders ---

    public Map<String, Boolean> computeMux4to1(boolean i0, boolean i1, boolean i2, boolean i3, boolean s1, boolean s0) {
        return new Multiplexer4to1().compute(Map.of(
            "I0", i0, "I1", i1, "I2", i2, "I3", i3,
            "S1", s1, "S0", s0
        ));
    }

    public Map<String, Boolean> computeDecoder3to8(boolean enable, boolean a2, boolean a1, boolean a0) {
        return new Decoder3to8().compute(Map.of("E", enable, "A2", a2, "A1", a1, "A0", a0));
    }

    public Map<String, Boolean> computePriorityEncoder8to3(Map<String, Boolean> inputs) {
        return new PriorityEncoder8to3().compute(inputs);
    }

    public MagnitudeComparator.Result computeMagnitudeComparison(int a, int b) {
        return MagnitudeComparator.compare4Bit(a, b);
    }

    // --- Boolean Logic & K-Map Engine ---

    public TruthTable generateTruthTable(List<String> variableNames, Function<boolean[], Boolean> evaluator) {
        return new TruthTable(variableNames, evaluator);
    }

    public int[][] solveKMap(int numVariables, Set<Integer> minterms) {
        KMapSolver solver = new KMapSolver(numVariables);
        solver.populateMinterms(minterms);
        return solver.getGrid();
    }
}