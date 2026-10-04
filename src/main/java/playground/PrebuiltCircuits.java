package playground;

import java.util.List;

/** A few ready-made demo circuits built only from supported components. */
public final class PrebuiltCircuits {

    private PrebuiltCircuits() { }

    /** Names for a UI drop-down. */
    public static List<String> names() {
        return List.of("AND", "OR", "XOR", "NAND (NOT of AND)", "NOT-AND-NOR combo");
    }

    public static Circuit byName(String name) {
        return switch (name) {
            case "AND" -> twoInput(ComponentType.AND);
            case "OR" -> twoInput(ComponentType.OR);
            case "XOR" -> twoInput(ComponentType.XOR);
            case "NAND (NOT of AND)" -> nandFromAndNot();
            case "NOT-AND-NOR combo" -> combo();
            default -> throw new IllegalArgumentException("Unknown prebuilt circuit: " + name);
        };
    }

    /** A, B -> gate -> Y. */
    public static Circuit twoInput(ComponentType gate) {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 50, 50);
        String b = c.addComponent(ComponentType.INPUT, 50, 150);
        String g = c.addComponent(gate, 250, 100);
        String y = c.addComponent(ComponentType.OUTPUT, 450, 100);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(y, "Y");
        c.connect(a, g, 0);
        c.connect(b, g, 1);
        c.connect(g, y, 0);
        return c;
    }

    /** A, B -> AND -> NOT -> Y  (same truth table as NAND). */
    public static Circuit nandFromAndNot() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 50, 50);
        String b = c.addComponent(ComponentType.INPUT, 50, 150);
        String and = c.addComponent(ComponentType.AND, 200, 100);
        String not = c.addComponent(ComponentType.NOT, 350, 100);
        String y = c.addComponent(ComponentType.OUTPUT, 500, 100);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(y, "Y");
        c.connect(a, and, 0);
        c.connect(b, and, 1);
        c.connect(and, not, 0);
        c.connect(not, y, 0);
        return c;
    }

    /** Y = NOR(NOT A, AND(A, B)), which simplifies to A AND (NOT B). Shows branching and several gate levels. */
    public static Circuit combo() {
        Circuit c = new Circuit();
        String a = c.addComponent(ComponentType.INPUT, 50, 50);
        String b = c.addComponent(ComponentType.INPUT, 50, 200);
        String not = c.addComponent(ComponentType.NOT, 200, 50);
        String and = c.addComponent(ComponentType.AND, 200, 200);
        String nor = c.addComponent(ComponentType.NOR, 350, 125);
        String y = c.addComponent(ComponentType.OUTPUT, 500, 125);
        c.setLabel(a, "A");
        c.setLabel(b, "B");
        c.setLabel(y, "Y");
        c.connect(a, not, 0);
        c.connect(a, and, 0);
        c.connect(b, and, 1);
        c.connect(not, nor, 0);
        c.connect(and, nor, 1);
        c.connect(nor, y, 0);
        return c;
    }
}
