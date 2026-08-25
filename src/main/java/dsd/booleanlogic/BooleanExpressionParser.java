package dsd.booleanlogic;

import java.util.*;

public class BooleanExpressionParser {

    private static final Map<Character, Integer> PRECEDENCE = Map.of(
        '!', 3,
        '&', 2,
        '^', 2,
        '|', 1
    );

    /**
     * Extracts distinct variable names from a boolean expression string in alphabetical order.
     */
    public static List<String> extractVariables(String expression) {
        Set<String> vars = new TreeSet<>();
        for (char ch : expression.toCharArray()) {
            if (Character.isLetter(ch)) {
                vars.add(String.valueOf(ch).toUpperCase());
            }
        }
        return new ArrayList<>(vars);
    }

    /**
     * Evaluates a boolean infix expression against variable values.
     * Example: expression = "A & !B", values = {"A": true, "B": false} -> returns true
     */
    public static boolean evaluate(String expression, Map<String, Boolean> variableValues) {
        List<String> rpn = infixToPostfix(expression);
        return evaluatePostfix(rpn, variableValues);
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Deque<Character> operatorStack = new ArrayDeque<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isWhitespace(ch)) {
                continue;
            }

            if (Character.isLetter(ch)) {
                output.add(String.valueOf(ch).toUpperCase());
            } else if (ch == '(') {
                operatorStack.push(ch);
            } else if (ch == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    output.add(String.valueOf(operatorStack.pop()));
                }
                if (operatorStack.isEmpty() || operatorStack.pop() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses in expression.");
                }
            } else if (PRECEDENCE.containsKey(ch)) {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(' &&
                       PRECEDENCE.getOrDefault(operatorStack.peek(), 0) >= PRECEDENCE.get(ch)) {
                    // Right-associative handling for unary NOT
                    if (ch == '!' && operatorStack.peek() == '!') {
                        break;
                    }
                    output.add(String.valueOf(operatorStack.pop()));
                }
                operatorStack.push(ch);
            } else {
                throw new IllegalArgumentException("Unsupported character in expression: " + ch);
            }
        }

        while (!operatorStack.isEmpty()) {
            char op = operatorStack.pop();
            if (op == '(' || op == ')') {
                throw new IllegalArgumentException("Mismatched parentheses in expression.");
            }
            output.add(String.valueOf(op));
        }

        return output;
    }

    private static boolean evaluatePostfix(List<String> rpn, Map<String, Boolean> variableValues) {
        Deque<Boolean> evaluationStack = new ArrayDeque<>();

        for (String token : rpn) {
            if (token.length() == 1 && PRECEDENCE.containsKey(token.charAt(0))) {
                char op = token.charAt(0);
                if (op == '!') {
                    if (evaluationStack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid operand for NOT operator.");
                    }
                    evaluationStack.push(!evaluationStack.pop());
                } else {
                    if (evaluationStack.size() < 2) {
                        throw new IllegalArgumentException("Insufficient operands for operator: " + op);
                    }
                    boolean right = evaluationStack.pop();
                    boolean left = evaluationStack.pop();

                    switch (op) {
                        case '&' -> evaluationStack.push(left && right);
                        case '|' -> evaluationStack.push(left || right);
                        case '^' -> evaluationStack.push(left ^ right);
                        default -> throw new IllegalStateException("Unexpected operator: " + op);
                    }
                }
            } else {
                // Token is a variable
                Boolean val = variableValues.get(token);
                if (val == null) {
                    throw new IllegalArgumentException("Missing value for variable: " + token);
                }
                evaluationStack.push(val);
            }
        }

        if (evaluationStack.size() != 1) {
            throw new IllegalArgumentException("Invalid Boolean expression.");
        }

        return evaluationStack.pop();
    }
}