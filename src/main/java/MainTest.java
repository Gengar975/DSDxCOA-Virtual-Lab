import coa.alu.Alu;
import coa.alu.AluOperation;
import coa.alu.AluResult;

public class MainTest {

    public static void main(String[] args) {

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