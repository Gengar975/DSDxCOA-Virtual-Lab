import dsd.booleanlogic.KMapSolver;
import dsd.booleanlogic.TruthTable;
import java.util.HashSet;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== 3-Variable Majority Function Test ===");
        
        // Evaluates true if at least two inputs are true
        TruthTable tt = new TruthTable(List.of("A", "B", "C"), inputs -> {
            int count = (inputs[0] ? 1 : 0) + (inputs[1] ? 1 : 0) + (inputs[2] ? 1 : 0);
            return count >= 2;
        });

        System.out.println("Canonical SOP: " + tt.toSopExpression());
        System.out.println("Canonical POS: " + tt.toPosExpression());

        System.out.println("\n=== K-Map Population ===");
        KMapSolver kmap = new KMapSolver(3);
        kmap.populateMinterms(new HashSet<>(tt.getMinterms()));
        kmap.printGrid();
    }
}