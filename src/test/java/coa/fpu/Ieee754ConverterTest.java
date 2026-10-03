package coa.fpu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Ieee754ConverterTest {

    @Test
    public void testPositiveValue() {
        Ieee754Result res = Ieee754Converter.toIeee754(1.0);
        assertEquals("0x3F800000", res.getHexString());
        assertEquals(0, res.getSign());
        assertEquals("NORMAL", res.getCategory());
    }

    @Test
    public void testNegativeValue() {
        Ieee754Result res = Ieee754Converter.toIeee754(-1.0);
        assertEquals("0xBF800000", res.getHexString());
        assertEquals(1, res.getSign());
    }

    @Test
    public void testPositiveZero() {
        Ieee754Result res = Ieee754Converter.toIeee754(0.0);
        assertEquals("ZERO", res.getCategory());
        assertEquals(0, res.getSign());
    }

    @Test
    public void testNegativeZero() {
        Ieee754Result res = Ieee754Converter.toIeee754(-0.0);
        assertEquals("NEGATIVE_ZERO", res.getCategory());
        assertEquals(1, res.getSign());
    }

    @Test
    public void testFractionalValue() {
        Ieee754Result res = Ieee754Converter.toIeee754(10.5);
        assertEquals("0x41280000", res.getHexString());
    }

    @Test
    public void testRounding() {
        Ieee754Result res = Ieee754Converter.toIeee754(3.14);
        assertEquals("0x4048F5C3", res.getHexString());
    }

    @Test
    public void testPositiveInfinity() {
        Ieee754Result res = Ieee754Converter.toIeee754(Double.POSITIVE_INFINITY);
        assertEquals("POSITIVE_INFINITY", res.getCategory());
    }

    @Test
    public void testNegativeInfinity() {
        Ieee754Result res = Ieee754Converter.toIeee754(Double.NEGATIVE_INFINITY);
        assertEquals("NEGATIVE_INFINITY", res.getCategory());
    }

    @Test
    public void testNaN() {
        Ieee754Result res = Ieee754Converter.toIeee754(Double.NaN);
        assertEquals("NAN", res.getCategory());
    }

    @Test
    public void testBoundaryLargeValue() {
        Ieee754Result res = Ieee754Converter.toIeee754(100000.0);
        assertEquals("0x47C35000", res.getHexString());
    }

    @Test
    public void testRoundTripDecode() {
        Ieee754Result forward = Ieee754Converter.toIeee754(1.0);
        Ieee754Result decoded = Ieee754Converter.fromIeee754(forward.getBitPattern());
        assertEquals(1.0, decoded.getOriginalValue(), 0.0001);
        assertEquals("NORMAL", decoded.getCategory());
    }
}
