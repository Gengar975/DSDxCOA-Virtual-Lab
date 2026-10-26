package lab.ui;

import lab.content.Topic;

/** Chooses the visual lab for each topic. */
final class Labs {

    private Labs() { }

    static Lab create(Topic t) {
        String id = t.getId();
        return switch (id) {
            case "and", "or", "not", "nand", "nor", "xor", "xnor", "half-adder", "full-adder", "half-sub", "full-sub" -> new CircuitLab(t);
            case "expr" -> new ExprLab();
            case "kmap" -> new KMapLab();
            case "num-base", "num-gray", "num-bcd", "num-ascii" -> new NumberLab(id);
            case "ripple" -> new RippleLab();
            case "decoder" -> BlockLab.decoder();
            case "mux" -> BlockLab.mux();
            case "priority" -> BlockLab.priority();
            case "comparator" -> BlockLab.comparator();
            case "bcd-adder" -> BlockLab.bcdAdder();
            case "clock" -> new ClockLab();
            case "sr-ff" -> new FlipFlopLab("sr");
            case "d-ff" -> new FlipFlopLab("d");
            case "jk-ff" -> new FlipFlopLab("jk");
            case "t-ff" -> new FlipFlopLab("t");
            case "ms-ff" -> new FlipFlopLab("ms");
            case "ff-convert" -> new FlipFlopConvertLab();
            case "counter" -> new CounterLab();
            case "shift-reg" -> new ShiftLab();
            case "booth" -> new ArithmeticLab("booth");
            case "restoring" -> new ArithmeticLab("restoring");
            case "non-restoring" -> new ArithmeticLab("non-restoring");
            case "ieee754" -> new IeeeLab();
            case "alu" -> new AluLab();
            case "registers" -> new RegisterLab();
            case "memory" -> new MemoryLab();
            case "cache" -> new CacheLab();
            default -> throw new IllegalArgumentException("No lab for topic " + id);
        };
    }

    /** Heading for the table section of a topic. */
    static String tableTitle(Topic t) {
        return switch (t.getGroup()) {
            case GATES, COMBINATIONAL -> t.getId().equals("mux") || t.getId().equals("decoder") || t.getId().equals("priority") || t.getId().equals("comparator") || t.getId().equals("bcd-adder") ? "Function table" : "Truth table";
            case SEQUENTIAL -> t.getId().equals("counter") ? "Counting sequence" : "Characteristic table";
            default -> "Table";
        };
    }
}
