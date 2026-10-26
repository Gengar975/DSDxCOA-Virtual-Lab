package lab.model;

import playground.Circuit;
import playground.ComponentType;
import playground.PrebuiltCircuits;

/**
 * Gate-level diagrams used by the lessons. They are real {@link Circuit} objects, so the
 * lessons evaluate them with the same engine as the playground (and the engine's gates).
 */
public final class CircuitLibrary {

    private CircuitLibrary() { }

    /** Diagram for a gate lesson id ("and", "or", ...), or the adder/subtractor ids. */
    public static Circuit forTopic(String id) {
        return switch (id) {
            case "and" -> PrebuiltCircuits.twoInput(ComponentType.AND);
            case "or" -> PrebuiltCircuits.twoInput(ComponentType.OR);
            case "nand" -> PrebuiltCircuits.twoInput(ComponentType.NAND);
            case "nor" -> PrebuiltCircuits.twoInput(ComponentType.NOR);
            case "xor" -> PrebuiltCircuits.twoInput(ComponentType.XOR);
            case "xnor" -> PrebuiltCircuits.twoInput(ComponentType.XNOR);
            case "not" -> notGate();
            case "half-adder" -> halfAdder();
            case "full-adder" -> fullAdder();
            case "half-sub" -> halfSubtractor();
            case "full-sub" -> fullSubtractor();
            default -> throw new IllegalArgumentException("No gate diagram for " + id);
        };
    }

    public static boolean hasDiagram(String id) {
        try {
            forTopic(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Simpler gates that behave exactly like the given gate, or null when there is no such circuit. */
    public static Circuit equivalent(String id) {
        return switch (id) {
            case "nand" -> PrebuiltCircuits.nandFromAndNot();
            case "nor" -> chain(ComponentType.OR, "nor");
            case "xnor" -> chain(ComponentType.XOR, "xnor");
            case "xor" -> xorFromBasics();
            default -> null;
        };
    }

    /** A, B -> gate -> NOT -> Y. */
    private static Circuit chain(ComponentType first, String id) {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 40, 40);
        String b = c.addComponent(ComponentType.INPUT, 40, 150);
        String g = c.addComponent(first, 220, 85);
        String n = c.addComponent(ComponentType.NOT, 380, 95);
        String y = c.addComponent(ComponentType.OUTPUT, 520, 100);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(y, "Y");
        c.connect(a, g, 0);
        c.connect(b, g, 1);
        c.connect(g, n, 0);
        c.connect(n, y, 0);
        return c;
    }

    /** Y = A'B + AB' built from NOT, AND and OR gates. */
    private static Circuit xorFromBasics() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 20, 30);
        String b = c.addComponent(ComponentType.INPUT, 20, 200);
        String na = c.addComponent(ComponentType.NOT, 130, 20);
        String nb = c.addComponent(ComponentType.NOT, 130, 210);
        String t1 = c.addComponent(ComponentType.AND, 290, 40);
        String t2 = c.addComponent(ComponentType.AND, 290, 170);
        String or = c.addComponent(ComponentType.OR, 450, 105);
        String y = c.addComponent(ComponentType.OUTPUT, 590, 115);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(y, "Y");
        c.connect(a, na, 0);
        c.connect(b, nb, 0);
        c.connect(na, t1, 0);
        c.connect(b, t1, 1);
        c.connect(a, t2, 0);
        c.connect(nb, t2, 1);
        c.connect(t1, or, 0);
        c.connect(t2, or, 1);
        c.connect(or, y, 0);
        return c;
    }

    static Circuit notGate() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 60, 80);
        String n = c.addComponent(ComponentType.NOT, 280, 80);
        String y = c.addComponent(ComponentType.OUTPUT, 500, 80);
        c.setLabel(a, "A");
        c.setLabel(y, "Y");
        c.connect(a, n, 0);
        c.connect(n, y, 0);
        return c;
    }

    /** Sum = A xor B, Carry = A and B. */
    public static Circuit halfAdder() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 30, 40);
        String b = c.addComponent(ComponentType.INPUT, 30, 190);
        String x = c.addComponent(ComponentType.XOR, 280, 30);
        String n = c.addComponent(ComponentType.AND, 280, 180);
        String s = c.addComponent(ComponentType.OUTPUT, 520, 40);
        String co = c.addComponent(ComponentType.OUTPUT, 520, 190);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(s, "SUM");
        c.setLabel(co, "CARRY");
        c.connect(a, x, 0);
        c.connect(b, x, 1);
        c.connect(a, n, 0);
        c.connect(b, n, 1);
        c.connect(x, s, 0);
        c.connect(n, co, 0);
        return c;
    }

    /** Two half adders and an OR gate. */
    public static Circuit fullAdder() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 20, 30);
        String b = c.addComponent(ComponentType.INPUT, 20, 130);
        String cin = c.addComponent(ComponentType.INPUT, 20, 270);
        String x1 = c.addComponent(ComponentType.XOR, 190, 50);
        String a1 = c.addComponent(ComponentType.AND, 190, 160);
        String x2 = c.addComponent(ComponentType.XOR, 400, 80);
        String a2 = c.addComponent(ComponentType.AND, 400, 220);
        String or = c.addComponent(ComponentType.OR, 590, 180);
        String s = c.addComponent(ComponentType.OUTPUT, 600, 90);
        String co = c.addComponent(ComponentType.OUTPUT, 720, 190);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(cin, "CIN");
        c.setLabel(s, "SUM");
        c.setLabel(co, "COUT");
        c.connect(a, x1, 0);
        c.connect(b, x1, 1);
        c.connect(a, a1, 0);
        c.connect(b, a1, 1);
        c.connect(x1, x2, 0);
        c.connect(cin, x2, 1);
        c.connect(x1, a2, 0);
        c.connect(cin, a2, 1);
        c.connect(a1, or, 0);
        c.connect(a2, or, 1);
        c.connect(x2, s, 0);
        c.connect(or, co, 0);
        return c;
    }

    /** Difference = A xor B, Borrow = (not A) and B. */
    public static Circuit halfSubtractor() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 30, 40);
        String b = c.addComponent(ComponentType.INPUT, 30, 220);
        String x = c.addComponent(ComponentType.XOR, 300, 30);
        String na = c.addComponent(ComponentType.NOT, 160, 130);
        String n = c.addComponent(ComponentType.AND, 300, 200);
        String d = c.addComponent(ComponentType.OUTPUT, 540, 40);
        String bo = c.addComponent(ComponentType.OUTPUT, 540, 210);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(d, "DIFF");
        c.setLabel(bo, "BORROW");
        c.connect(a, x, 0);
        c.connect(b, x, 1);
        c.connect(a, na, 0);
        c.connect(na, n, 0);
        c.connect(b, n, 1);
        c.connect(x, d, 0);
        c.connect(n, bo, 0);
        return c;
    }

    /** Difference = A xor B xor Bin, Bout = A'B + (A xor B)'Bin. */
    public static Circuit fullSubtractor() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 20, 30);
        String b = c.addComponent(ComponentType.INPUT, 20, 150);
        String bin = c.addComponent(ComponentType.INPUT, 20, 290);
        String x1 = c.addComponent(ComponentType.XOR, 170, 40);
        String x2 = c.addComponent(ComponentType.XOR, 380, 60);
        String na = c.addComponent(ComponentType.NOT, 170, 150);
        String t1 = c.addComponent(ComponentType.AND, 320, 160);
        String nx = c.addComponent(ComponentType.NOT, 300, 250);
        String t2 = c.addComponent(ComponentType.AND, 440, 260);
        String or = c.addComponent(ComponentType.OR, 600, 200);
        String d = c.addComponent(ComponentType.OUTPUT, 590, 70);
        String bo = c.addComponent(ComponentType.OUTPUT, 720, 210);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(bin, "BIN");
        c.setLabel(d, "DIFF");
        c.setLabel(bo, "BOUT");
        c.connect(a, x1, 0);
        c.connect(b, x1, 1);
        c.connect(x1, x2, 0);
        c.connect(bin, x2, 1);
        c.connect(a, na, 0);
        c.connect(na, t1, 0);
        c.connect(b, t1, 1);
        c.connect(x1, nx, 0);
        c.connect(nx, t2, 0);
        c.connect(bin, t2, 1);
        c.connect(t1, or, 0);
        c.connect(t2, or, 1);
        c.connect(x2, d, 0);
        c.connect(or, bo, 0);
        return c;
    }
}
