package lab.ui;

import coa.cpu.Accumulator;
import coa.cpu.InstructionRegister;
import coa.cpu.ProgramCounter;
import coa.cpu.Register;
import java.util.ArrayDeque;
import java.util.Deque;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** CPU registers: a general register, the program counter, the accumulator and the instruction register (8 bits each). */
final class RegisterLab implements Lab {

    private static final int W = 8;

    private final Register general = new Register("R1", W);
    private final ProgramCounter pc = new ProgramCounter(W);
    private final Accumulator acc = new Accumulator(W);
    private final InstructionRegister ir = new InstructionRegister(W);
    private final ComboBox<String> which = Kit.combo("General register R1", "Program Counter (PC)", "Accumulator (ACC)", "Instruction Register (IR)");
    private final BitCells cells = new BitCells(W, false);
    private final Label value = Kit.plain("", "card-title");
    private final Label status = Kit.label("", "status-info");
    private final Label log = Kit.label("", "formula");
    private final TextField write = Kit.field("value 0-255", 110);
    private final TextField offset = Kit.field("offset", 90);
    private final HBox pcRow;
    private final Deque<String> history = new ArrayDeque<>();

    RegisterLab() {
        write.setText("42");
        offset.setText("5");
        pcRow = new HBox(10, Kit.button("PC + 1 (next instruction)", "btn-secondary", e -> act(() -> pc.increment(), "PC incremented")),
            Kit.plain("Jump by", "lab-title"), offset,
            Kit.button("Jump (advance)", "btn-secondary", e -> act(() -> pc.advance(Long.parseLong(offset.getText().trim())), "PC advanced by " + offset.getText().trim())));
        pcRow.setAlignment(Pos.CENTER_LEFT);
        which.setOnAction(e -> {
            boolean isPc = which.getSelectionModel().getSelectedIndex() == 1;
            pcRow.setVisible(isPc);
            pcRow.setManaged(isPc);
            show(false);
        });
        pcRow.setVisible(false);
        pcRow.setManaged(false);
        write.setOnAction(e -> act(() -> current().write(Long.parseLong(write.getText().trim())), "wrote " + write.getText().trim()));
        show(false);
    }

    private Register current() {
        return switch (which.getSelectionModel().getSelectedIndex()) {
            case 1 -> pc;
            case 2 -> acc;
            case 3 -> ir;
            default -> general;
        };
    }

    private void act(Runnable r, String what) {
        try {
            r.run();
            status.setText("");
            history.addFirst(which.getValue() + ": " + what + " → " + current().read());
            while (history.size() > 6) history.removeLast();
            log.setText(String.join("\n", history));
            show(true);
        } catch (NumberFormatException ex) {
            status.setText("✖ Type a whole number.");
            status.getStyleClass().setAll("status-bad");
        } catch (RuntimeException ex) {
            status.setText("✖ " + ex.getMessage());
            status.getStyleClass().setAll("status-bad");
        }
    }

    private void show(boolean animate) {
        Register r = current();
        cells.set(r.toBinaryString(), animate);
        value.setText(r.getName() + " = " + r.read() + "   (hex " + Long.toHexString(r.read()).toUpperCase() + ")");
    }

    @Override
    public Node diagram() {
        VBox pcBox = box("PC", "Program Counter", "holds the address of the NEXT instruction; +1 after every fetch");
        VBox irBox = box("IR", "Instruction Register", "holds the instruction that is being executed right now");
        VBox accBox = box("ACC", "Accumulator", "holds the results of ALU calculations");
        return new HBox(14, pcBox, irBox, accBox);
    }

    private static VBox box(String abbr, String name, String text) {
        Label a = Kit.plain(abbr, "card-title");
        VBox v = new VBox(4, a, Kit.plain(name, "lab-title"), Kit.label(text, "muted"));
        v.getStyleClass().add("lab-panel");
        v.setPrefWidth(260);
        return v;
    }

    @Override
    public Node interactive() {
        HBox bar = new HBox(10, which, write, Kit.button("Write", "btn-primary", e -> act(() -> current().write(Long.parseLong(write.getText().trim())), "wrote " + write.getText().trim())),
            Kit.button("Clear", "btn-secondary", e -> act(() -> current().clear(), "cleared")));
        bar.setAlignment(Pos.CENTER_LEFT);
        return new VBox(14, Kit.label("Pick a register, write a value (0 - 255) and watch its 8 flip-flops change. The PC has extra buttons for the next-instruction and jump operations.", "muted"),
            bar, pcRow, status, cells, value, Kit.plain("Recent actions", "lab-title"), log);
    }
}
