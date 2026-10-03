package dsd.booleanlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

public class TruthTable {
    private final List<String> variableNames;
    private final List<boolean[]> inputRows;
    private final List<Boolean> outputValues;

    public TruthTable(List<String> variableNames, Function<boolean[], Boolean> logicEvaluator) {
        this.variableNames = variableNames;
        this.inputRows = new ArrayList<>();
        this.outputValues = new ArrayList<>();

        int numVars = variableNames.size();
        int totalCombinations = 1 << numVars; // 2^N combinations

        for (int i = 0; i < totalCombinations; i++) {
            boolean[] row = new boolean[numVars];
            for (int bit = 0; bit < numVars; bit++) {
                row[bit] = ((i >> (numVars - 1 - bit)) & 1) == 1;
            }
            this.inputRows.add(row);
            this.outputValues.add(logicEvaluator.apply(row));
        }
    }

    /**
     * Constructs a TruthTable by parsing an arbitrary Boolean string expression (e.g. "(A & B) | !C").
     */
    public TruthTable(String expression) {
        this(
            BooleanExpressionParser.extractVariables(expression),
            inputs -> {
                List<String> vars = BooleanExpressionParser.extractVariables(expression);
                Map<String, Boolean> valMap = new HashMap<>();
                for (int i = 0; i < vars.size(); i++) {
                    valMap.put(vars.get(i), inputs[i]);
                }
                return BooleanExpressionParser.evaluate(expression, valMap);
            }
        );
    }

    public List<Integer> getMinterms() {
        List<Integer> minterms = new ArrayList<>();
        for (int i = 0; i < outputValues.size(); i++) {
            if (Boolean.TRUE.equals(outputValues.get(i))) {
                minterms.add(i);
            }
        }
        return minterms;
    }

    public List<Integer> getMaxterms() {
        List<Integer> maxterms = new ArrayList<>();
        for (int i = 0; i < outputValues.size(); i++) {
            if (Boolean.FALSE.equals(outputValues.get(i))) {
                maxterms.add(i);
            }
        }
        return maxterms;
    }

    public String toSopExpression() {
        List<Integer> minterms = getMinterms();
        if (minterms.isEmpty()) return "0";
        if (minterms.size() == outputValues.size()) return "1";
        return "Σm(" + minterms.toString().replaceAll("[\\[\\]]", "") + ")";
    }

    public String toPosExpression() {
        List<Integer> maxterms = getMaxterms();
        if (maxterms.isEmpty()) return "1";
        if (maxterms.size() == outputValues.size()) return "0";
        return "ΠM(" + maxterms.toString().replaceAll("[\\[\\]]", "") + ")";
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public List<boolean[]> getInputRows() {
        return inputRows;
    }

    public List<Boolean> getOutputValues() {
        return outputValues;
    }
}