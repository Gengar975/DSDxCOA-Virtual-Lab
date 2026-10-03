import coa.alu.Alu;
import coa.alu.AluOperation;
import coa.alu.AluResult;

import dsd.booleanlogic.KMapSolver;
import dsd.booleanlogic.TruthTable;

import java.util.HashSet;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {

        // =====================================================
        // ALU TESTS
        // =====================================================

        Alu alu = new Alu();

        // 4-bit operands
        boolean[] A = {false, true, false, true};  // 0101 = 5
        boolean[] B = {false, false, true, true};  // 0011 = 3

        // ==============================
        // TEST 1: ADD
        // ==============================
        System.out.println("========================================");
        System.out.println("TEST 1: ADD");
        System.out.println("========================================");

        AluResult addResult = alu.compute(A, B, AluOperation.ADD);
        printResult(addResult);

        // ==============================
        // TEST 2: SUB
        // ==============================
        System.out.println("\n========================================");
        System.out.println("TEST 2: SUB");
        System.out.println("========================================");

        AluResult subResult = alu.compute(A, B, AluOperation.SUB);
        printResult(subResult);

        // ==============================
        // TEST 3: AND
        // ==============================
        System.out.println("\n========================================");
        System.out.println("TEST 3: AND");
        System.out.println("========================================");

        AluResult andResult = alu.compute(A, B, AluOperation.AND);
        printResult(andResult);

        // ==============================
        // TEST 4: OR
        // ==============================
        System.out.println("\n========================================");
        System.out.println("TEST 4: OR");
        System.out.println("========================================");

        AluResult orResult = alu.compute(A, B, AluOperation.OR);
        printResult(orResult);

        // ==============================
        // TEST 5: XOR
        // ==============================
        System.out.println("\n========================================");
        System.out.println("TEST 5: XOR");
        System.out.println("========================================");

        AluResult xorResult = alu.compute(A, B, AluOperation.XOR);
        printResult(xorResult);

        // ==============================
        // TEST 6: NOT
        // ==============================
        System.out.println("\n========================================");
        System.out.println("TEST 6: NOT");
        System.out.println("========================================");

        AluResult notResult = alu.compute(A, AluOperation.NOT);
        printResult(notResult);


        // =====================================================
        // TRUTH TABLE + K-MAP TEST
        // =====================================================

        System.out.println("\n\n========================================");
        System.out.println("TRUTH TABLE + K-MAP TEST");
        System.out.println("========================================");

        System.out.println("\n=== 3-Variable Majority Function Test ===");

        // Evaluates true if at least two inputs are true
        TruthTable tt = new TruthTable(List.of("A", "B", "C"), inputs -> {
            int count = (inputs[0] ? 1 : 0)
                      + (inputs[1] ? 1 : 0)
                      + (inputs[2] ? 1 : 0);

            return count >= 2;
        });

        System.out.println("Canonical SOP: " + tt.toSopExpression());
        System.out.println("Canonical POS: " + tt.toPosExpression());

        System.out.println("\n=== K-Map Population ===");

        KMapSolver kmap = new KMapSolver(3);
        kmap.populateMinterms(new HashSet<>(tt.getMinterms()));
        kmap.printGrid();
    }


    private static void printResult(AluResult result) {

        System.out.println("Operand A     : " + result.getOperandA());
        System.out.println("Operand B     : " + result.getOperandB());
        System.out.println("Operation     : " + result.getOperation());
        System.out.println("Result        : " + result.getResultBinary());

        System.out.println("\n--- Flags ---");
        System.out.println("Carry Flag    : " + (result.isCarryFlag() ? 1 : 0));
        System.out.println("Zero Flag     : " + (result.isZeroFlag() ? 1 : 0));
        System.out.println("Sign Flag     : " + (result.isSignFlag() ? 1 : 0));
        System.out.println("Overflow Flag : " + (result.isOverflowFlag() ? 1 : 0));

        System.out.println("\n--- Step-by-Step Trace ---");

        for (String step : result.getSteps()) {
            System.out.println(step);
        }
    }
}