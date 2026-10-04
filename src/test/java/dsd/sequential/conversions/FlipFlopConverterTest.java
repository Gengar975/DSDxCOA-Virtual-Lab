package dsd.sequential.conversions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlipFlopConverterTest {

    @Test
    public void testDToSR() {
        SRInputs r0 = FlipFlopConverter.dToSR(0);
        assertEquals(0, r0.getS());
        assertEquals(1, r0.getR());

        SRInputs r1 = FlipFlopConverter.dToSR(1);
        assertEquals(1, r1.getS());
        assertEquals(0, r1.getR());
    }

    @Test
    public void testTToSR() {
        // T=1, Q=0 -> S=1, R=0 (set)
        SRInputs r = FlipFlopConverter.tToSR(1, 0);
        assertEquals(1, r.getS());
        assertEquals(0, r.getR());

        // T=1, Q=1 -> S=0, R=1 (reset)
        SRInputs r2 = FlipFlopConverter.tToSR(1, 1);
        assertEquals(0, r2.getS());
        assertEquals(1, r2.getR());

        // T=0 -> S=0, R=0 regardless of Q (hold)
        SRInputs r3 = FlipFlopConverter.tToSR(0, 1);
        assertEquals(0, r3.getS());
        assertEquals(0, r3.getR());
    }

    @Test
    public void testDToJK() {
        JKInputs k0 = FlipFlopConverter.dToJK(0);
        assertEquals(0, k0.getJ());
        assertEquals(1, k0.getK());

        JKInputs k1 = FlipFlopConverter.dToJK(1);
        assertEquals(1, k1.getJ());
        assertEquals(0, k1.getK());
    }

    @Test
    public void testTToJK() {
        JKInputs k = FlipFlopConverter.tToJK(1);
        assertEquals(1, k.getJ());
        assertEquals(1, k.getK());

        JKInputs k0 = FlipFlopConverter.tToJK(0);
        assertEquals(0, k0.getJ());
        assertEquals(0, k0.getK());
    }

    @Test
    public void testJkToD() {
        // J=1,K=0,Q=0 -> next state 1 -> D=1
        assertEquals(1, FlipFlopConverter.jkToD(1, 0, 0).getValue());
        // J=0,K=1,Q=1 -> next state 0 -> D=0
        assertEquals(0, FlipFlopConverter.jkToD(0, 1, 1).getValue());
        // J=1,K=1,Q=1 -> toggle -> next state 0 -> D=0
        assertEquals(0, FlipFlopConverter.jkToD(1, 1, 1).getValue());
        // J=0,K=0,Q=1 -> hold -> next state 1 -> D=1
        assertEquals(1, FlipFlopConverter.jkToD(0, 0, 1).getValue());
    }

    @Test
    public void testTToD() {
        // T=1,Q=0 -> next state 1 -> D=1
        assertEquals(1, FlipFlopConverter.tToD(1, 0).getValue());
        // T=0,Q=1 -> next state 1 -> D=1
        assertEquals(1, FlipFlopConverter.tToD(0, 1).getValue());
    }

    @Test
    public void testDToT() {
        // D=1,Q=0 -> need T=1 to go 0->1
        assertEquals(1, FlipFlopConverter.dToT(1, 0).getValue());
        // D=1,Q=1 -> need T=0 to stay at 1
        assertEquals(0, FlipFlopConverter.dToT(1, 1).getValue());
    }

    @Test
    public void testJkToT() {
        // J=1,K=0,Q=0 -> next state should be 1 -> T=1 (0 XOR 1=1)
        assertEquals(1, FlipFlopConverter.jkToT(1, 0, 0).getValue());
        // J=0,K=1,Q=1 -> next state should be 0 -> T=1 (1 XOR 0 = needs T=1)
        assertEquals(1, FlipFlopConverter.jkToT(0, 1, 1).getValue());
        // J=0,K=0,Q=1 -> hold -> next state 1 -> T=0
        assertEquals(0, FlipFlopConverter.jkToT(0, 0, 1).getValue());
    }

    @Test
    public void testInvalidBitThrows() {
        assertThrows(IllegalArgumentException.class, () -> FlipFlopConverter.dToSR(2));
    }
}
