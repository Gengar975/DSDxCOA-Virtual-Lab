package dsd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import dsd.numbers.ConversionResult;
import dsd.numbers.NumberConverter;

public class NumberConverterTest {

    @Test
    public void testDecimalToBinary() {
        ConversionResult result = NumberConverter.decimalToBinary(13);
        assertEquals("1101", result.getConvertedValue());
        assertFalse(result.getSteps().isEmpty());
    }

    @Test
    public void testBinaryToGray() {
        ConversionResult result = NumberConverter.binaryToGray("1101");
        assertEquals("1011", result.getConvertedValue());
    }

    @Test
    public void testDecimalToExcess3() {
        ConversionResult result = NumberConverter.decimalToExcess3("47");
        assertEquals("0111 1010", result.getConvertedValue());
    }
    
    @Test
    public void testDecimalToHexadecimal() {
        ConversionResult res = NumberConverter.decimalToHexadecimal(254);
        assertEquals("FE", res.getConvertedValue());
    }

    @Test
    public void testOctalToDecimal() {
        ConversionResult res = NumberConverter.octalToDecimal("75"); // 7*8 + 5 = 61
        assertEquals("61", res.getConvertedValue());
    }

    @Test
    public void testTextToAsciiBinary() {
        ConversionResult res = NumberConverter.textToAsciiBinary("A");
        assertEquals("01000001", res.getConvertedValue()); // ASCII for 'A' is 65
    }
}