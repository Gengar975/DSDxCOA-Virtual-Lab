package dsd.numbers;

import java.util.ArrayList;
import java.util.List;

public class NumberConverter {

    // --- Decimal to Binary / Octal / Hex ---

    public static ConversionResult decimalToBinary(long decimal) {
        return decimalToRadix(decimal, 2);
    }

    public static ConversionResult decimalToOctal(long decimal) {
        return decimalToRadix(decimal, 8);
    }

    public static ConversionResult decimalToHexadecimal(long decimal) {
        return decimalToRadix(decimal, 16);
    }

    private static ConversionResult decimalToRadix(long decimal, int radix) {
        if (decimal < 0) {
            throw new IllegalArgumentException("Only non-negative integers are supported.");
        }
        if (decimal == 0) {
            return new ConversionResult("0", "0", List.of("0 / " + radix + " = 0, Remainder = 0"));
        }

        List<String> steps = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        char[] digits = "0123456789ABCDEF".toCharArray();
        long temp = decimal;

        while (temp > 0) {
            long remainder = temp % radix;
            long quotient = temp / radix;
            char digitChar = digits[(int) remainder];
            steps.add(temp + " / " + radix + " = " + quotient + ", Remainder = " + remainder + 
                      (radix == 16 && remainder >= 10 ? " ('" + digitChar + "')" : ""));
            result.insert(0, digitChar);
            temp = quotient;
        }

        return new ConversionResult(String.valueOf(decimal), result.toString(), steps);
    }

    // --- Binary / Octal / Hex to Decimal ---

    public static ConversionResult binaryToDecimal(String binary) {
        return radixToDecimal(binary, 2, "[01]+", "Binary");
    }

    public static ConversionResult octalToDecimal(String octal) {
        return radixToDecimal(octal, 8, "[0-7]+", "Octal");
    }

    public static ConversionResult hexadecimalToDecimal(String hex) {
        return radixToDecimal(hex.toUpperCase(), 16, "[0-9A-F]+", "Hexadecimal");
    }

    private static ConversionResult radixToDecimal(String value, int radix, String regex, String radixName) {
        if (value == null || value.isEmpty() || !value.matches(regex)) {
            throw new IllegalArgumentException("Invalid " + radixName + " input: " + value);
        }

        List<String> steps = new ArrayList<>();
        long total = 0;
        int length = value.length();

        for (int i = 0; i < length; i++) {
            char ch = value.charAt(i);
            int digitVal = Character.digit(ch, radix);
            int power = length - 1 - i;
            long term = (long) digitVal * (long) Math.pow(radix, power);
            total += term;
            steps.add("Digit '" + ch + "' (" + digitVal + ") * " + radix + "^" + power + " = " + term);
        }

        steps.add("Sum = " + total);
        return new ConversionResult(value, String.valueOf(total), steps);
    }

    // --- Special Codes: Gray, BCD, Excess-3, ASCII ---

    public static ConversionResult binaryToGray(String binary) {
        validateBinaryString(binary);

        List<String> steps = new ArrayList<>();
        StringBuilder gray = new StringBuilder();
        
        char msb = binary.charAt(0);
        gray.append(msb);
        steps.add("Gray MSB = Binary MSB = " + msb);

        for (int i = 1; i < binary.length(); i++) {
            int bPrev = binary.charAt(i - 1) - '0';
            int bCurr = binary.charAt(i) - '0';
            int gBit = bPrev ^ bCurr;
            gray.append(gBit);
            steps.add("Bit " + i + ": " + bPrev + " XOR " + bCurr + " = " + gBit);
        }

        return new ConversionResult(binary, gray.toString(), steps);
    }

    public static ConversionResult decimalToBcd(String decimalStr) {
        if (!decimalStr.matches("\\d+")) {
            throw new IllegalArgumentException("Input must contain only decimal digits (0-9).");
        }

        List<String> steps = new ArrayList<>();
        StringBuilder bcd = new StringBuilder();

        for (int i = 0; i < decimalStr.length(); i++) {
            int digit = decimalStr.charAt(i) - '0';
            String bit4 = String.format("%4s", Integer.toBinaryString(digit)).replace(' ', '0');
            bcd.append(bit4).append(i < decimalStr.length() - 1 ? " " : "");
            steps.add("Digit '" + digit + "' -> 4-bit BCD: " + bit4);
        }

        return new ConversionResult(decimalStr, bcd.toString(), steps);
    }

    public static ConversionResult decimalToExcess3(String decimalStr) {
        if (!decimalStr.matches("\\d+")) {
            throw new IllegalArgumentException("Input must contain only decimal digits (0-9).");
        }

        List<String> steps = new ArrayList<>();
        StringBuilder excess3 = new StringBuilder();

        for (int i = 0; i < decimalStr.length(); i++) {
            int digit = decimalStr.charAt(i) - '0';
            int shifted = digit + 3;
            String bit4 = String.format("%4s", Integer.toBinaryString(shifted)).replace(' ', '0');
            excess3.append(bit4).append(i < decimalStr.length() - 1 ? " " : "");
            steps.add("Digit '" + digit + "' + 3 = " + shifted + " -> 4-bit binary: " + bit4);
        }

        return new ConversionResult(decimalStr, excess3.toString(), steps);
    }

    public static ConversionResult textToAsciiBinary(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }

        List<String> steps = new ArrayList<>();
        StringBuilder binary = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int asciiCode = (int) ch;
            String bit8 = String.format("%8s", Integer.toBinaryString(asciiCode)).replace(' ', '0');
            binary.append(bit8).append(i < text.length() - 1 ? " " : "");
            steps.add("Char '" + ch + "' -> ASCII Decimal: " + asciiCode + " -> 8-bit Binary: " + bit8);
        }

        return new ConversionResult(text, binary.toString(), steps);
    }

    private static void validateBinaryString(String binary) {
        if (binary == null || binary.isEmpty() || !binary.matches("[01]+")) {
            throw new IllegalArgumentException("Input must be a non-empty string of 0s and 1s.");
        }
    }
}