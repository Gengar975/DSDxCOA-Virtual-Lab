package dsd.sequential.conversions;

/**
 * Derives the excitation inputs needed to make one flip-flop type emulate
 * another, from their classical excitation (characteristic) tables.
 */
public final class FlipFlopConverter {

    private FlipFlopConverter() {
        // Utility class - no objects needed.
    }

    /*
     * D flip-flop using SR flip-flop
     *
     * S = D
     * R = D'
     */
    public static SRInputs dToSR(int d) {
        validateBit(d);

        return new SRInputs(
                d,
                d == 0 ? 1 : 0
        );
    }

    /*
     * T flip-flop using SR flip-flop
     *
     * S = T.Q'
     * R = T.Q
     */
    public static SRInputs tToSR(int t, int q) {
        validateBit(t);
        validateBit(q);

        int qBar = q == 0 ? 1 : 0;

        return new SRInputs(
                t * qBar,
                t * q
        );
    }

    /*
     * D flip-flop using JK flip-flop
     *
     * J = D
     * K = D'
     */
    public static JKInputs dToJK(int d) {
        validateBit(d);

        return new JKInputs(
                d,
                d == 0 ? 1 : 0
        );
    }

    /*
     * T flip-flop using JK flip-flop
     *
     * J = T
     * K = T
     */
    public static JKInputs tToJK(int t) {
        validateBit(t);

        return new JKInputs(t, t);
    }

    /*
     * JK flip-flop using D flip-flop
     *
     * D = J.Q' + K'.Q
     */
    public static SingleInput jkToD(int j, int k, int q) {
        validateBit(j);
        validateBit(k);
        validateBit(q);

        int qBar = q == 0 ? 1 : 0;
        int kBar = k == 0 ? 1 : 0;

        int d = (j * qBar) | (kBar * q);

        return new SingleInput(d);
    }

    /*
     * T flip-flop using D flip-flop
     *
     * D = T XOR Q
     */
    public static SingleInput tToD(int t, int q) {
        validateBit(t);
        validateBit(q);

        int d = t ^ q;

        return new SingleInput(d);
    }

    /*
     * D flip-flop using T flip-flop
     *
     * T = D XOR Q
     */
    public static SingleInput dToT(int d, int q) {
        validateBit(d);
        validateBit(q);

        int t = d ^ q;

        return new SingleInput(t);
    }

    /*
     * JK flip-flop using T flip-flop
     *
     * T = J.Q' + K.Q
     */
    public static SingleInput jkToT(int j, int k, int q) {
        validateBit(j);
        validateBit(k);
        validateBit(q);

        int qBar = q == 0 ? 1 : 0;

        int t = (j * qBar) | (k * q);

        return new SingleInput(t);
    }

    private static void validateBit(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                    "Input must be either 0 or 1."
            );
        }
    }
}
