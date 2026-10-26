package lab.content;

import static lab.content.Topic.Group.COA;
import static lab.content.Topic.Group.PLAYGROUND;

import java.util.ArrayList;
import java.util.List;

/** Lessons: computer organization and architecture, plus the playground. */
final class ComputerOrgContent {

    private ComputerOrgContent() { }

    static List<Topic> topics() {
        List<Topic> t = new ArrayList<>();

        t.add(Topic.of("booth", COA, "Booth's Multiplication", "Multiply signed binary numbers using add, subtract and shift")
            .theory(
                "Multiplying two signed binary numbers with simple shift-and-add does not work well for negative numbers. Booth's algorithm handles positive and negative numbers (in 2's complement) with the same procedure.",
                "It uses three registers: A (accumulator, starts at 0), Q (the multiplier) and M (the multiplicand), plus an extra bit Q-1 (starts at 0). In each of n steps it looks at the pair Q0 Q-1: 01 means A = A + M, 10 means A = A - M, and 00 or 11 mean do nothing. Then A, Q and Q-1 are shifted right arithmetically (the sign bit is kept). After n steps the product is in A and Q together.")
            .points("Pair Q0 Q-1 = 01: add M.   10: subtract M.   00 / 11: no operation.",
                    "Always followed by an arithmetic right shift of A, Q, Q-1.",
                    "The product has 2n bits, stored in A followed by Q.",
                    "It speeds up multiplication when the multiplier has long runs of 1s.")
            .uses("Hardware multipliers in processors.")
            .example("Worked example: 3 x -2 (4-bit)",
                     "M = 0011 (3), Q = 1110 (-2), A = 0000, Q-1 = 0.",
                     "In each of the 4 iterations look at Q0 Q-1, add or subtract M if needed, then shift right.",
                     "Final A:Q = 11111010 = -6, which equals 3 x -2. Run the lab below and step through it."));

        t.add(Topic.of("restoring", COA, "Restoring Division", "Divide by trial subtraction, restoring A when it goes negative")
            .theory(
                "Binary division works like long division by hand. Registers: Q holds the dividend (it will become the quotient), M the divisor and A the running remainder (starts at 0).",
                "Each step: shift A and Q left by one bit, subtract M from A. If A is now negative the subtraction was too much, so RESTORE A by adding M back and set the quotient bit to 0; otherwise keep the result and set the quotient bit to 1. After n steps, Q is the quotient and A is the remainder.")
            .points("Shift left, subtract M, test the sign of A.",
                    "Negative: restore A (add M back), Q0 = 0.", "Non-negative: keep A, Q0 = 1.",
                    "n bits need n iterations.")
            .uses("Integer division units in CPUs.")
            .example("Worked example: 13 / 3 (4-bit)",
                     "Q = 1101, M = 00011, A = 00000.",
                     "Four iterations produce quotient bits 0, 1, 0, 0.",
                     "Quotient = 4, remainder = 1 (check: 3 x 4 + 1 = 13)."));

        t.add(Topic.of("non-restoring", COA, "Non-Restoring Division", "A faster variant that never restores: it adds or subtracts alternately")
            .theory(
                "Restoring division wastes time adding M back after a failed subtraction. Non-restoring division avoids this. It uses the sign of A: if A is non-negative, shift left and SUBTRACT M; if A is negative, shift left and ADD M. The quotient bit is 1 when the new A is non-negative, otherwise 0.",
                "Because the correction is merged into the next step, each iteration needs only one add or subtract. At the end, if A is negative, one final correction (A = A + M) gives the true remainder.")
            .points("A >= 0: shift, then A = A - M.", "A < 0: shift, then A = A + M.",
                    "Quotient bit = 1 if A >= 0 after the operation.", "Final correction if A < 0.")
            .uses("Faster hardware dividers.")
            .example("Worked example: 13 / 3",
                     "Each iteration does one add or one subtract only (no restoring step).",
                     "After 4 iterations a final correction makes A = 1.",
                     "Quotient = 4, remainder = 1, same answer as restoring division."));

        t.add(Topic.of("ieee754", COA, "IEEE 754 Floating Point", "How computers store real numbers in 32 bits")
            .theory(
                "Integers are easy, but real numbers such as 6.25 need a way to move the binary point. IEEE 754 stores a number in scientific notation using 32 bits: 1 SIGN bit, 8 EXPONENT bits and 23 MANTISSA (fraction) bits.",
                "The value is (-1)^sign x 1.mantissa x 2^(exponent - 127). The leading 1 is implied and not stored. The exponent is stored with a BIAS of 127 so it can represent negative powers without a sign bit.")
            .points("Layout: 1 | 8 | 23 bits.", "Bias = 127.", "Normalised form 1.xxxx x 2^e.",
                    "Special values: zero, infinity, NaN, subnormal numbers.")
            .uses("All floating-point arithmetic in CPUs and GPUs.")
            .example("Worked example: -6.25",
                     "6.25 = 110.01 in binary = 1.1001 x 2^2.",
                     "Sign = 1, exponent = 2 + 127 = 129 = 10000001, mantissa = 10010000...0.",
                     "Result: 1 10000001 10010000000000000000000 = 0xC0C80000."));

        t.add(Topic.of("alu", COA, "Arithmetic Logic Unit (ALU)", "The part of the CPU that calculates")
            .theory(
                "The ALU performs arithmetic (add, subtract) and logic (AND, OR, XOR, NOT) on binary operands. A control code selects which operation is performed. Inside, it reuses the adders and gates you met earlier.",
                "Besides the result, the ALU produces STATUS FLAGS that describe it: Carry (C) when an addition produces a carry out, Zero (Z) when the result is 0, Sign (S) when the result is negative, and Overflow (V) when a signed result does not fit in the number of bits.")
            .points("Operations: ADD, SUB, AND, OR, XOR, NOT.", "Flags: Carry, Zero, Sign, Overflow.",
                    "Subtraction is A + (2's complement of B).", "Overflow: signed result too large for the bit width.")
            .uses("Every instruction in a CPU that calculates something.")
            .example("Worked example: 1011 + 0110 (4-bit)",
                     "Binary sum = 10001, but only 4 bits are kept: 0001, with Carry = 1.",
                     "Result is not zero, sign bit is 0.",
                     "As unsigned numbers 11 + 6 = 17 does not fit in 4 bits, hence the carry."));

        t.add(Topic.of("registers", COA, "CPU Registers", "Small, very fast storage inside the processor")
            .theory(
                "Registers are tiny memories built from flip-flops that sit inside the CPU. They hold the data the processor is working on right now. A few have special jobs: the PROGRAM COUNTER (PC) holds the address of the next instruction, the ACCUMULATOR holds intermediate arithmetic results, and the INSTRUCTION REGISTER (IR) holds the instruction being executed.",
                "After each instruction is fetched, the PC is incremented to point at the next one. A jump instruction simply loads a new address into the PC.")
            .points("Register = group of flip-flops sharing one clock.", "PC: address of the next instruction.",
                    "Accumulator: result of ALU operations.", "IR: current instruction.")
            .uses("Fetch-decode-execute cycle.")
            .example("Worked example: PC starts at 0",
                     "Fetch instruction at address 0, PC becomes 1.",
                     "Fetch instruction at address 1, PC becomes 2.",
                     "A jump to address 10 loads 10 into the PC."));

        t.add(Topic.of("memory", COA, "Main Memory", "A numbered array of storage locations")
            .theory(
                "Main memory is a long list of storage cells, each with its own number called an ADDRESS. The CPU can WRITE a value to an address or READ the value stored there. The number of addresses is the capacity.",
                "Memory is much slower than the CPU, which is why caches and registers exist.")
            .points("Each location has a unique address.", "Read does not change the stored value; write replaces it.",
                    "Capacity = number of addressable locations.")
            .uses("Storing programs and data.")
            .example("Worked example: write 42 to address 3, then read address 3",
                     "Write: memory[3] = 42.",
                     "Read: asking for address 3 returns 42.",
                     "Other addresses are not affected."));

        t.add(Topic.of("cache", COA, "Cache Memory", "A small fast memory that keeps recently used data close to the CPU")
            .theory(
                "Main memory is slow. A cache is a small, fast memory placed between the CPU and main memory that keeps copies of recently used data. If the data is in the cache it is a HIT (fast); if not, it is a MISS and the data must be fetched from main memory and loaded into the cache.",
                "In a DIRECT-MAPPED cache, every memory address maps to exactly one cache line. The address is split into three fields: the OFFSET (which byte inside the line), the INDEX (which cache line) and the TAG (identifies which memory block currently occupies the line). A hit needs the line to be valid AND its stored tag to equal the address tag.")
            .points("Address = Tag | Index | Offset.", "Index selects the line; tag confirms it is the right data.",
                    "Hit: tag matches. Miss: load the block, store the new tag.", "Hit rate = hits / accesses.")
            .uses("Every modern CPU has several cache levels.")
            .example("Worked example: 4 lines, 4 bytes per line, address 37",
                     "Offset = 37 mod 4 = 1; block number = 37 / 4 = 9.",
                     "Index = 9 mod 4 = 1; Tag = 9 / 4 = 2.",
                     "If line 1 is empty or holds another tag, it is a MISS and tag 2 is loaded."));

        t.add(Topic.of("playground", PLAYGROUND, "Circuit Playground", "Drag, drop and wire logic gates to build your own circuits")
            .theory(
                "Now it is your turn. Drag gates from the palette onto the canvas, connect them with wires and flip the inputs to watch the signals travel through your circuit. Green wires carry 1, grey wires carry 0.")
            .points("Drag a component from the left palette onto the canvas.",
                    "Drag from an output dot to an input dot to draw a wire.",
                    "Click an INPUT switch to toggle it.", "Select a component and press Delete to remove it.")
            .uses("Try the ready-made circuits, then build your own half adder."));
        return t;
    }
}
