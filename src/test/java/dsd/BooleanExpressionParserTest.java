package dsd;

import dsd.booleanlogic.BooleanExpressionParser;
import dsd.booleanlogic.TruthTable;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class BooleanExpressionParserTest {

    @Test
    public void testSimpleExpressionEvaluation() {
        // A AND (NOT B)
        String expr = "A & !B";
        assertTrue(BooleanExpressionParser.evaluate(expr, Map.of("A", true, "B", false)));
        assertFalse(BooleanExpressionParser.evaluate(expr, Map.of("A", true, "B", true)));
    }

    @Test
    public void testTruthTableFromExpression() {
        // XOR equivalence: (A & !B) | (!A & B)
        String expr = "(A & !B) | (!A & B)";
        TruthTable tt = new TruthTable(expr);

        assertEquals(List.of("A", "B"), tt.getVariableNames());
        assertEquals("Σm(1, 2)", tt.toSopExpression());
        assertEquals("ΠM(0, 3)", tt.toPosExpression());
    }
}