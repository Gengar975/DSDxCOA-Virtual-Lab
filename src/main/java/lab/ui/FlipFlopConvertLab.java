package lab.ui;

import dsd.sequential.conversions.FlipFlopConverter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Flip-flop conversion: pick a conversion and see the excitation table produced by the project's FlipFlopConverter. */
final class FlipFlopConvertLab implements Lab {

    private static final String[] OPTIONS = {
        "D from SR flip-flop", "T from SR flip-flop", "D from JK flip-flop", "T from JK flip-flop",
        "JK from D flip-flop", "T from D flip-flop", "D from T flip-flop", "JK from T flip-flop"};

    private final ComboBox<String> choice = Kit.combo(OPTIONS);
    private final VBox tableBox = new VBox(12);
    private final Label explain = Kit.label("", "body-text");

    FlipFlopConvertLab() {
        choice.setOnAction(e -> build());
        build();
    }

    private static String bit(int v) { return String.valueOf(v); }

    private void build() {
        List<String[]> rows = new ArrayList<>();
        List<String> head;
        int outs;
        String text;
        switch (choice.getSelectionModel().getSelectedIndex()) {
            case 0 -> {
                head = List.of("D", "S", "R"); outs = 2;
                for (int dv = 0; dv < 2; dv++) { var r = FlipFlopConverter.dToSR(dv); rows.add(new String[] {bit(dv), bit(r.getS()), bit(r.getR())}); }
                text = "To make an SR flip-flop work like a D flip-flop: S = D and R = D'. D = 1 sets it, D = 0 resets it, and S = R = 1 can never happen.";
            }
            case 1 -> {
                head = List.of("T", "Q", "S", "R"); outs = 2;
                for (int tv = 0; tv < 2; tv++) for (int qv = 0; qv < 2; qv++) { var r = FlipFlopConverter.tToSR(tv, qv); rows.add(new String[] {bit(tv), bit(qv), bit(r.getS()), bit(r.getR())}); }
                text = "To toggle with an SR flip-flop: S = T · Q' and R = T · Q. When T = 1, set it if it is 0 and reset it if it is 1.";
            }
            case 2 -> {
                head = List.of("D", "J", "K"); outs = 2;
                for (int dv = 0; dv < 2; dv++) { var r = FlipFlopConverter.dToJK(dv); rows.add(new String[] {bit(dv), bit(r.getJ()), bit(r.getK())}); }
                text = "A JK flip-flop acts like a D flip-flop when J = D and K = D'.";
            }
            case 3 -> {
                head = List.of("T", "J", "K"); outs = 2;
                for (int tv = 0; tv < 2; tv++) { var r = FlipFlopConverter.tToJK(tv); rows.add(new String[] {bit(tv), bit(r.getJ()), bit(r.getK())}); }
                text = "A JK flip-flop acts like a T flip-flop when J = K = T.";
            }
            case 4 -> {
                head = List.of("J", "K", "Q", "D"); outs = 1;
                for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++) for (int qv = 0; qv < 2; qv++)
                    rows.add(new String[] {bit(j), bit(k), bit(qv), bit(FlipFlopConverter.jkToD(j, k, qv).getValue())});
                text = "A D flip-flop can act like a JK flip-flop if D = J·Q' + K'·Q (the JK characteristic equation).";
            }
            case 5 -> {
                head = List.of("T", "Q", "D"); outs = 1;
                for (int tv = 0; tv < 2; tv++) for (int qv = 0; qv < 2; qv++) rows.add(new String[] {bit(tv), bit(qv), bit(FlipFlopConverter.tToD(tv, qv).getValue())});
                text = "A D flip-flop can act like a T flip-flop if D = T ⊕ Q.";
            }
            case 6 -> {
                head = List.of("D", "Q", "T"); outs = 1;
                for (int dv = 0; dv < 2; dv++) for (int qv = 0; qv < 2; qv++) rows.add(new String[] {bit(dv), bit(qv), bit(FlipFlopConverter.dToT(dv, qv).getValue())});
                text = "A T flip-flop can act like a D flip-flop if T = D ⊕ Q: toggle only when the new value differs from the stored one.";
            }
            default -> {
                head = List.of("J", "K", "Q", "T"); outs = 1;
                for (int j = 0; j < 2; j++) for (int k = 0; k < 2; k++) for (int qv = 0; qv < 2; qv++)
                    rows.add(new String[] {bit(j), bit(k), bit(qv), bit(FlipFlopConverter.jkToT(j, k, qv).getValue())});
                text = "A T flip-flop can act like a JK flip-flop if T = J·Q' + K·Q.";
            }
        }
        explain.setText(text);
        TruthGrid g = new TruthGrid(head, outs, rows);
        g.reveal(120);
        tableBox.getChildren().setAll(g);
    }

    @Override
    public Node diagram() {
        return Kit.label("Method: (1) write down the flip-flop you WANT, (2) look at the excitation table of the flip-flop you HAVE, (3) work out what its inputs must be for every present state Q and wanted next state, (4) simplify with a K-map.", "body-text");
    }

    @Override
    public Node interactive() {
        return new VBox(12, Kit.label("Choose a conversion. The table shows the inputs the available flip-flop must receive.", "muted"), choice, explain, tableBox);
    }
}
