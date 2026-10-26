package lab.content;

import static lab.content.Topic.Group.BOOLEAN;
import static lab.content.Topic.Group.GATES;
import static lab.content.Topic.Group.NUMBERS;

import java.util.ArrayList;
import java.util.List;

/** Lessons: logic gates, Boolean algebra, number systems and codes. */
final class BasicsContent {

    private BasicsContent() { }

    static List<Topic> topics() {
        List<Topic> t = new ArrayList<>();

        t.add(Topic.of("and", GATES, "AND Gate", "The output is 1 only when ALL inputs are 1")
            .theory(
                "A logic gate is the smallest building block of a digital circuit. It takes one or more inputs, each either 0 (LOW, no voltage) or 1 (HIGH, voltage), and produces a single output that depends only on those inputs.",
                "The AND gate behaves like two switches in series: current flows only if switch A AND switch B are both closed. In Boolean algebra it is written Y = A · B (the dot means AND, like multiplication: 1·1 = 1, anything · 0 = 0).")
            .points("Output is 1 only when every input is 1.",
                    "Written Y = A · B or Y = AB.",
                    "Multiplying by 0 always gives 0, so a single 0 input forces the output to 0.",
                    "A 2-input AND has 4 input combinations and only 1 of them gives output 1.")
            .uses("Enabling a signal only when two conditions are true (for example, 'door closed AND key inserted').",
                  "Masking bits in a processor: A AND 0 = 0 clears a bit, A AND 1 keeps it.",
                  "Carry generation inside adders.")
            .example("Worked example: A = 1, B = 0",
                     "Step 1: Write the expression  Y = A · B.",
                     "Step 2: Put in the values  Y = 1 · 0.",
                     "Step 3: Multiply  Y = 0.  One input is 0, so the AND gate outputs 0."));

        t.add(Topic.of("or", GATES, "OR Gate", "The output is 1 when AT LEAST ONE input is 1")
            .theory(
                "The OR gate behaves like two switches in parallel: current flows if switch A OR switch B (or both) is closed. It only outputs 0 when every input is 0.",
                "In Boolean algebra the OR operation is written Y = A + B. Be careful: this '+' means OR, not arithmetic addition, so 1 + 1 = 1.")
            .points("Output is 0 only when every input is 0.",
                    "Written Y = A + B.",
                    "A single 1 input forces the output to 1.",
                    "1 + 1 = 1 in Boolean algebra (a result can never be 2).")
            .uses("Raising an alarm if ANY sensor is triggered.",
                  "Combining several conditions that can all start the same action.",
                  "Setting bits in a register: A OR 1 = 1.")
            .example("Worked example: A = 0, B = 1",
                     "Step 1: Y = A + B.",
                     "Step 2: Y = 0 + 1.",
                     "Step 3: Y = 1.  At least one input is 1, so the OR gate outputs 1."));

        t.add(Topic.of("not", GATES, "NOT Gate (Inverter)", "The output is the opposite of the input")
            .theory(
                "The NOT gate, also called an inverter, has exactly one input and one output. It simply flips the signal: 0 becomes 1 and 1 becomes 0.",
                "It is written Y = A' or Y = Ā (read 'A bar' or 'not A'). In circuit diagrams the inversion is shown as a small circle (a 'bubble') on the gate. That bubble is also added to other gates to build NAND, NOR and XNOR.")
            .points("Only gate with a single input.",
                    "Y = Ā: the output is always the complement of the input.",
                    "Applying NOT twice gives back the original value (Ā' = A).",
                    "The bubble on a gate symbol means 'inverted'.")
            .uses("Turning an active-high signal into an active-low one.",
                  "Building NAND, NOR and XNOR gates.",
                  "Creating the complement of a variable for Boolean expressions.")
            .example("Worked example: A = 1",
                     "Step 1: Y = Ā.",
                     "Step 2: A is 1, so its complement is 0.",
                     "Step 3: Y = 0.  Applying NOT again returns 1."));

        t.add(Topic.of("nand", GATES, "NAND Gate", "AND followed by NOT - a universal gate")
            .theory(
                "NAND means NOT-AND. It works exactly like an AND gate whose output is then inverted, so it outputs 0 only when all inputs are 1 and 1 in every other case. Its symbol is an AND gate with a bubble on the output.",
                "NAND is called a universal gate: any other gate (NOT, AND, OR ...) can be built using only NAND gates. That is why NAND chips are cheap and very common in real hardware.")
            .points("Y = (A · B)'.",
                    "Truth table is the exact opposite of AND.",
                    "Universal gate: NOT = NAND with inputs tied together; AND = NAND followed by NAND-as-NOT.",
                    "De Morgan: (A · B)' = A' + B'.")
            .uses("Building every other gate in memory chips and processors.",
                  "Latches and flip-flops (two cross-coupled NAND gates form an SR latch).")
            .example("Worked example: A = 1, B = 1",
                     "Step 1: AND first: 1 · 1 = 1.",
                     "Step 2: Invert the result: NOT 1 = 0.",
                     "Step 3: Y = 0.  NAND is 0 only when both inputs are 1."));

        t.add(Topic.of("nor", GATES, "NOR Gate", "OR followed by NOT - also a universal gate")
            .theory(
                "NOR means NOT-OR. It is an OR gate with an inverted output, so it gives 1 only when every input is 0. Its symbol is an OR gate with a bubble on the output.",
                "Like NAND, NOR is a universal gate: you can build every other gate from NOR gates alone.")
            .points("Y = (A + B)'.",
                    "Output is 1 only for input 00.",
                    "Universal gate.",
                    "De Morgan: (A + B)' = A' · B'.")
            .uses("Alarm and control circuits that must be true only when nothing is active.",
                  "SR latches built from NOR gates.")
            .example("Worked example: A = 0, B = 0",
                     "Step 1: OR first: 0 + 0 = 0.",
                     "Step 2: Invert: NOT 0 = 1.",
                     "Step 3: Y = 1.  NOR outputs 1 only when both inputs are 0."));

        t.add(Topic.of("xor", GATES, "XOR Gate", "The output is 1 when the inputs are DIFFERENT")
            .theory(
                "XOR (exclusive OR) outputs 1 when an odd number of inputs are 1. For two inputs this means the output is 1 when A and B are different, and 0 when they are the same.",
                "It is written Y = A ⊕ B = A'B + AB'. XOR is the heart of binary addition: the sum bit of adding two bits is exactly A ⊕ B.")
            .points("Y = A ⊕ B = A'B + AB'.",
                    "Output 1 when the inputs differ.",
                    "A ⊕ 0 = A and A ⊕ 1 = A' (XOR can pass or invert a bit).",
                    "A ⊕ A = 0, which is used to compare two values.")
            .uses("Sum bit in half adders and full adders.",
                  "Parity generators and error detection.",
                  "Comparing two bits for equality (inverted).")
            .example("Worked example: A = 1, B = 1",
                     "Step 1: Y = A ⊕ B.",
                     "Step 2: Inputs are the same, so they are not different.",
                     "Step 3: Y = 0.  Check with the formula: A'B + AB' = 0·1 + 1·0 = 0."));

        t.add(Topic.of("xnor", GATES, "XNOR Gate", "The output is 1 when the inputs are the SAME")
            .theory(
                "XNOR is XOR followed by NOT, which makes it an equality detector: it outputs 1 when both inputs are equal and 0 when they differ.",
                "It is written Y = (A ⊕ B)' = AB + A'B'. The symbol is the XOR gate with a bubble on the output.")
            .points("Y = (A ⊕ B)'.",
                    "Output 1 when A = B.",
                    "Truth table is the opposite of XOR.",
                    "Also called the equivalence gate.")
            .uses("Comparators that check whether two bits are equal.",
                  "Error checking circuits.")
            .example("Worked example: A = 0, B = 0",
                     "Step 1: XOR first: 0 ⊕ 0 = 0.",
                     "Step 2: Invert: NOT 0 = 1.",
                     "Step 3: Y = 1.  Equal inputs give 1."));

        t.add(Topic.of("expr", BOOLEAN, "Boolean Expressions & Truth Tables", "Describe any logic function with an expression and a table")
            .theory(
                "Boolean algebra is the mathematics of two values, 0 and 1. A Boolean expression combines variables with AND, OR and NOT to describe a logic circuit, for example Y = A · B' + C.",
                "A truth table lists the output for EVERY combination of inputs. With n variables there are 2ⁿ rows. From the table you can write the function in Sum of Products (SOP) form, by OR-ing the rows where the output is 1 (minterms), or in Product of Sums (POS) form, by AND-ing the rows where the output is 0 (maxterms).")
            .points("n variables give 2ⁿ rows in the truth table.",
                    "Minterm: an AND of all variables for a row whose output is 1 (m0, m1, m2 ...).",
                    "Maxterm: an OR of all variables for a row whose output is 0.",
                    "SOP = sum of minterms, POS = product of maxterms; both describe the same function.")
            .uses("Designing circuits from a requirement.",
                  "Checking that two different circuits do the same job.")
            .example("Worked example: Y = A · B' + C",
                     "Step 1: Three variables A, B, C, so 8 rows.",
                     "Step 2: Evaluate each row. Example row A=1, B=0, C=0: 1·1 + 0 = 1.",
                     "Step 3: Rows with output 1 are the minterms; OR them to get the SOP form."));

        t.add(Topic.of("kmap", BOOLEAN, "Karnaugh Map (K-Map)", "Simplify Boolean functions by grouping neighbouring 1s")
            .theory(
                "A Karnaugh map is a truth table rearranged into a grid so that neighbouring cells differ in only ONE variable. The row and column labels use Gray code (00, 01, 11, 10) instead of normal binary to keep this property.",
                "To simplify, circle groups of 1s. A group must be a rectangle containing 1, 2, 4, 8 ... cells (powers of two) and the map wraps around its edges. Bigger groups give simpler terms. In each group, variables that change disappear and variables that stay the same remain in the product term.")
            .points("Gray-code labels: only one bit changes between neighbours.",
                    "Group sizes must be 1, 2, 4, 8 or 16.",
                    "The grid wraps: left edge touches right edge, top touches bottom.",
                    "Use the fewest, largest groups; a 1 may be used in more than one group.")
            .uses("Reducing the number of gates in a design.",
                  "Hand-minimising functions of up to 4 variables.")
            .example("Worked example: minterms 1, 3 with 3 variables A B C",
                     "Step 1: Cells 001 and 011 are neighbours (only B changes).",
                     "Step 2: Group them. A = 0 and C = 1 stay constant, B changes.",
                     "Step 3: The group becomes the term A'C, so the function simplifies to Y = A'C."));

        t.add(Topic.of("num-base", NUMBERS, "Number Systems: Binary, Octal, Hex", "Convert between decimal and other bases")
            .theory(
                "We count in base 10 because we have ten fingers. Computers use base 2 (binary) because a transistor has two states. Each binary digit is a bit, and a position's weight doubles each step to the left: 8, 4, 2, 1.",
                "Binary numbers get long, so engineers group bits: 3 bits make one octal digit (base 8) and 4 bits make one hexadecimal digit (base 16, digits 0-9 and A-F). To convert decimal to another base, divide repeatedly by the base and read the remainders from bottom to top.")
            .points("Binary weights: ...128, 64, 32, 16, 8, 4, 2, 1.",
                    "1 hex digit = 4 bits, 1 octal digit = 3 bits.",
                    "Decimal to base b: divide by b, collect remainders, read them backwards.",
                    "Base b to decimal: multiply each digit by its weight and add.")
            .uses("Memory addresses are written in hex.",
                  "Colour codes, machine code and network addresses.")
            .example("Worked example: 13 to binary",
                     "13 / 2 = 6 remainder 1.",
                     "6 / 2 = 3 remainder 0,  3 / 2 = 1 remainder 1,  1 / 2 = 0 remainder 1.",
                     "Read the remainders from bottom to top: 1101.  Check: 8 + 4 + 0 + 1 = 13."));

        t.add(Topic.of("num-gray", NUMBERS, "Gray Code", "A binary code where only one bit changes at a time")
            .theory(
                "In ordinary binary, counting from 3 (011) to 4 (100) flips three bits at once. If the bits do not change at exactly the same instant, a wrong value can be read in between. Gray code avoids this: between any two consecutive numbers only ONE bit changes.",
                "To convert binary to Gray: keep the first (most significant) bit, then XOR each bit with the bit to its left. To convert back, XOR each Gray bit with the previously recovered binary bit.")
            .points("Consecutive values differ by exactly one bit.",
                    "Gray MSB = Binary MSB.",
                    "Gray bit i = Binary bit i XOR Binary bit (i - 1).",
                    "It is the labelling used on K-map edges.")
            .uses("Rotary encoders and position sensors.",
                  "K-map row and column labels.")
            .example("Worked example: binary 1011 to Gray",
                     "MSB stays: 1.",
                     "1 XOR 0 = 1,   0 XOR 1 = 1,   1 XOR 1 = 0.",
                     "Gray code = 1110."));

        t.add(Topic.of("num-bcd", NUMBERS, "BCD & Excess-3 Codes", "Represent each decimal digit with 4 bits")
            .theory(
                "Binary Coded Decimal (BCD) writes every decimal digit separately as a 4-bit binary number, so 259 becomes 0010 0101 1001. Only the patterns 0000 to 1001 are used; 1010 to 1111 are invalid in BCD. It makes decimal displays easy, at the cost of wasting some bit patterns.",
                "Excess-3 is BCD plus 3 for each digit (digit 5 becomes 5 + 3 = 8 = 1000). It is a self-complementing code, which makes subtraction in decimal hardware simpler.")
            .points("BCD uses 4 bits per decimal digit (0000 - 1001).",
                    "Codes 1010 - 1111 never appear in valid BCD.",
                    "Excess-3 = BCD + 0011.",
                    "BCD is not the same as pure binary: 12 is 0001 0010 in BCD but 1100 in binary.")
            .uses("Digital clocks, calculators and seven-segment displays.",
                  "Financial hardware where exact decimal digits matter.")
            .example("Worked example: decimal 25",
                     "Digit 2 -> BCD 0010, Excess-3 0101.",
                     "Digit 5 -> BCD 0101, Excess-3 1000.",
                     "BCD of 25 = 0010 0101."));

        t.add(Topic.of("num-ascii", NUMBERS, "ASCII Text Code", "How letters are stored as binary numbers")
            .theory(
                "Computers can only store numbers, so every character is given a number. ASCII (American Standard Code for Information Interchange) assigns a 7-bit number to each letter, digit and symbol, usually stored in 8 bits.",
                "For example 'A' is 65 = 01000001 and 'a' is 97 = 01100001. Notice upper and lower case differ by exactly one bit (value 32).")
            .points("ASCII uses 7 bits (0 - 127), usually stored in a byte.",
                    "'A' = 65, 'a' = 97, '0' = 48.",
                    "Upper and lower case differ by bit 5 (value 32).")
            .uses("Text files, keyboards and serial communication.")
            .example("Worked example: the letter 'H'",
                     "'H' has code 72.",
                     "72 = 64 + 8 = 01001000 in binary.",
                     "So 'H' is stored as 01001000."));
        return t;
    }
}
