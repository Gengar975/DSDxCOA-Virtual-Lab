package lab.content;

import static lab.content.Topic.Group.COMBINATIONAL;
import static lab.content.Topic.Group.SEQUENTIAL;

import java.util.ArrayList;
import java.util.List;

/** Lessons: combinational and sequential circuits. */
final class CircuitsContent {

    private CircuitsContent() { }

    static List<Topic> topics() {
        List<Topic> t = new ArrayList<>();

        t.add(Topic.of("half-adder", COMBINATIONAL, "Half Adder", "Adds two single bits and gives a Sum and a Carry")
            .theory(
                "A combinational circuit is one whose outputs depend ONLY on the present inputs; it has no memory. The half adder is the simplest arithmetic circuit: it adds two bits A and B.",
                "Adding bits gives 0+0=0, 0+1=1, 1+0=1 and 1+1=10 (binary two). The right digit is the SUM and the left digit is the CARRY. Looking at the truth table, SUM is an XOR gate and CARRY is an AND gate.")
            .points("Sum = A ⊕ B.", "Carry = A · B.",
                    "Built from just 1 XOR gate and 1 AND gate.",
                    "It is 'half' because it cannot accept a carry coming in from a previous stage.")
            .uses("The first (least significant) stage of an adder.", "Building blocks of full adders.")
            .example("Worked example: 1 + 1",
                     "Sum = 1 ⊕ 1 = 0.",
                     "Carry = 1 · 1 = 1.",
                     "Result: carry 1, sum 0 = binary 10 = decimal 2."));

        t.add(Topic.of("full-adder", COMBINATIONAL, "Full Adder", "Adds three bits: A, B and a Carry-in")
            .theory(
                "To add multi-bit numbers, every column after the first must add THREE bits: A, B and the carry that came from the column on the right. The full adder does exactly that and produces a Sum and a Carry-out.",
                "It can be built from two half adders and an OR gate: the first half adder adds A and B, the second adds that sum to Carry-in, and the two carries are OR-ed together.")
            .points("Sum = A ⊕ B ⊕ Cin.", "Cout = A·B + Cin·(A ⊕ B).",
                    "Two half adders + one OR gate.",
                    "Chain n full adders to add n-bit numbers.")
            .uses("Every adder inside a CPU's arithmetic unit.")
            .example("Worked example: 1 + 1 + 1",
                     "Sum = 1 ⊕ 1 ⊕ 1 = 1.",
                     "Cout = 1·1 + 1·(1 ⊕ 1) = 1 + 0 = 1.",
                     "Three ones add to binary 11 = decimal 3."));

        t.add(Topic.of("half-sub", COMBINATIONAL, "Half Subtractor", "Subtracts one bit from another: Difference and Borrow")
            .theory(
                "The half subtractor computes A - B for single bits. 0-0=0, 1-0=1, 1-1=0, but 0-1 does not fit, so we 'borrow' 1 from the next column: the result is 1 with a Borrow of 1.",
                "The Difference is again an XOR gate, but the Borrow is 1 only when A = 0 and B = 1, which is A' · B.")
            .points("Difference = A ⊕ B.", "Borrow = A' · B.",
                    "Uses 1 XOR, 1 NOT and 1 AND gate.")
            .uses("First stage of a subtractor.")
            .example("Worked example: 0 - 1",
                     "Difference = 0 ⊕ 1 = 1.",
                     "Borrow = 0' · 1 = 1.",
                     "We borrowed from the next column, so the result bit is 1 with borrow 1."));

        t.add(Topic.of("full-sub", COMBINATIONAL, "Full Subtractor", "Subtracts B and a Borrow-in from A")
            .theory(
                "Like the full adder, the full subtractor handles a borrow coming from the previous column. It computes A - B - Bin and outputs a Difference and a Borrow-out.",
                "It can be made from two half subtractors and an OR gate.")
            .points("Difference = A ⊕ B ⊕ Bin.", "Bout = A'·B + (A ⊕ B)'·Bin.",
                    "Chain them to subtract multi-bit numbers.")
            .uses("Subtraction in arithmetic units (though CPUs often use 2's-complement addition instead).")
            .example("Worked example: 0 - 0 - 1",
                     "Difference = 0 ⊕ 0 ⊕ 1 = 1.",
                     "Bout = 0'·0 + (0 ⊕ 0)'·1 = 0 + 1 = 1.",
                     "Borrow-in forces another borrow: result 1, Bout 1."));

        t.add(Topic.of("ripple", COMBINATIONAL, "Ripple Carry Adder / Subtractor", "Chain full adders to add or subtract n-bit numbers")
            .theory(
                "To add two 4-bit numbers, connect four full adders in a row. The carry-out of each stage is wired to the carry-in of the next, so the carry 'ripples' from right to left, just like carrying by hand.",
                "To subtract, XOR every bit of B with a control signal SUB and use SUB as the first carry-in. When SUB = 1 this forms the 2's complement of B (invert the bits and add 1), so the same hardware computes A - B.")
            .points("Cost: one full adder per bit.",
                    "Delay grows with the number of bits because each carry must wait for the previous one.",
                    "SUB = 1: B is inverted and Cin = 1, giving A + (-B).",
                    "Overflow flag: the carry into the sign bit differs from the carry out.")
            .uses("The adder section of the ALU.")
            .example("Worked example: 0110 + 0011",
                     "Bit 0: 0+1 = sum 1, carry 0.   Bit 1: 1+1 = sum 0, carry 1.",
                     "Bit 2: 1+0+carry 1 = sum 0, carry 1.   Bit 3: 0+0+carry 1 = sum 1, carry 0.",
                     "Result 1001 = 9, which equals 6 + 3."));

        t.add(Topic.of("bcd-adder", COMBINATIONAL, "BCD Adder", "Adds two decimal digits stored as 4-bit BCD")
            .theory(
                "A BCD digit is only valid from 0000 to 1001 (0-9). If you add two BCD digits with a normal binary adder, the result can exceed 9 and be wrong in BCD, for example 7 + 5 = 1100, which is not a BCD digit.",
                "The fix: whenever the binary sum is greater than 9 (or produced a carry), add 6 (0110) to skip the six unused patterns. The extra carry becomes the next decimal digit.")
            .points("Add the digits in binary first.", "If sum > 9 or carry = 1, add 0110.",
                    "The correction produces the decimal carry.")
            .uses("Calculators and digital meters that work in decimal.")
            .example("Worked example: 7 + 5",
                     "Binary: 0111 + 0101 = 1100 (12) which is greater than 9.",
                     "Add 6: 1100 + 0110 = 1 0010.",
                     "BCD result: carry 1, digit 2, which is 12."));

        t.add(Topic.of("comparator", COMBINATIONAL, "Magnitude Comparator", "Decides whether A is greater than, equal to or less than B")
            .theory(
                "A comparator takes two binary numbers and activates exactly one of three outputs: A > B, A = B, or A < B.",
                "The comparison starts at the most significant bit. If those bits differ, the larger bit decides the result immediately. If they are equal, move to the next bit, and continue until a difference is found or all bits are equal.")
            .points("Three outputs; exactly one is 1.", "Compare from MSB to LSB.",
                    "Bit equality uses XNOR gates.")
            .uses("Sorting hardware, conditional jumps in CPUs and threshold detection.")
            .example("Worked example: A = 1001 (9), B = 0101 (5)",
                     "MSB: A has 1, B has 0, so the bits differ.",
                     "The larger MSB wins immediately.",
                     "Result: A > B."));

        t.add(Topic.of("decoder", COMBINATIONAL, "3-to-8 Decoder", "Turns a 3-bit code into one active output line out of 8")
            .theory(
                "A decoder converts a binary code into a 'one-hot' signal: of its 2ⁿ output lines exactly one is 1, the line whose number equals the input code. The 3-to-8 decoder has inputs A2 A1 A0 and outputs D0 to D7.",
                "Each output is an AND of the inputs (or their complements) that matches its code. An ENABLE input switches the whole decoder on or off.")
            .points("3 inputs select 1 of 8 outputs.", "Exactly one output is 1 while enabled.",
                    "With ENABLE = 0 all outputs are 0.",
                    "Each output is a minterm of the inputs.")
            .uses("Memory address decoding.", "Selecting one device out of many on a bus.")
            .example("Worked example: A2 A1 A0 = 101",
                     "101 in binary is 5.",
                     "Output D5 goes to 1.",
                     "All other outputs stay at 0."));

        t.add(Topic.of("mux", COMBINATIONAL, "4-to-1 Multiplexer", "Chooses one of four inputs using 2 select lines")
            .theory(
                "A multiplexer (MUX) is a digital switch. It has several data inputs, some select lines, and one output. The select lines contain a binary number that picks which data input is passed to the output.",
                "A 4-to-1 multiplexer has data inputs I0 to I3 and select lines S1 S0. If S1 S0 = 10 (binary 2) then Y = I2.")
            .points("Y = S1'S0'·I0 + S1'S0·I1 + S1S0'·I2 + S1S0·I3.",
                    "2 select lines pick 1 of 4 inputs.", "A MUX can also implement any logic function.")
            .uses("Choosing between data sources inside a CPU.", "Sharing one wire among many signals.")
            .example("Worked example: I = 0110, S1 S0 = 10",
                     "I0=0, I1=1, I2=1, I3=0.",
                     "Select 10 means input number 2.",
                     "Y = I2 = 1."));

        t.add(Topic.of("priority", COMBINATIONAL, "8-to-3 Priority Encoder", "Reports the number of the highest active input")
            .theory(
                "An encoder is the opposite of a decoder: it converts one active line out of many into a binary code. A priority encoder also handles several inputs being active at once by always choosing the one with the HIGHEST number.",
                "The 8-to-3 priority encoder has inputs D0 to D7 and gives a 3-bit output plus a Valid flag that tells whether any input is active at all.")
            .points("Highest numbered active input wins.", "3-bit binary output.",
                    "Valid = 0 when no input is active.")
            .uses("Interrupt controllers: the most urgent request is served first.", "Keyboards.")
            .example("Worked example: D2 = 1 and D5 = 1",
                     "Two inputs are active at once.",
                     "D5 has the higher priority.",
                     "Output = 101 (5)."));

        t.add(Topic.of("clock", SEQUENTIAL, "Clock Signal", "The steady heartbeat that synchronises sequential circuits")
            .theory(
                "A combinational circuit forgets its inputs immediately. A sequential circuit has MEMORY: its output depends on its inputs AND on what happened before. To keep many memory elements working together, a clock signal alternates between 0 and 1 at a steady rate.",
                "Memory elements usually change state only at a clock EDGE: the rising edge (0 to 1) or the falling edge (1 to 0). Between edges, the stored value is held.")
            .points("The clock is a repeating square wave.", "Rising edge: 0 to 1; falling edge: 1 to 0.",
                    "Frequency = number of cycles per second (Hz).", "One pulse = one rising edge followed by one falling edge.")
            .uses("Every processor, counter and register is clocked.")
            .example("Worked example: a 1 MHz clock",
                     "1 MHz means one million cycles per second.",
                     "Period = 1 / 1,000,000 s = 1 microsecond.",
                     "A counter would advance once every microsecond."));

        t.add(Topic.of("sr-ff", SEQUENTIAL, "SR Flip-Flop", "The basic 1-bit memory: Set and Reset")
            .theory(
                "A flip-flop stores exactly one bit. The SR flip-flop has two inputs: S (Set) forces the stored bit Q to 1, and R (Reset) forces it to 0. When both are 0 it keeps its previous value.",
                "The combination S = 1 and R = 1 is forbidden because it forces both Q and Q' to the same value and the next state is unpredictable.")
            .points("S=0, R=0: hold.", "S=0, R=1: reset (Q=0).", "S=1, R=0: set (Q=1).", "S=1, R=1: invalid.")
            .uses("Switch debouncing.", "Foundation for all other flip-flops.")
            .example("Worked example: Q = 0, then S = 1, R = 0",
                     "Set input is active, so Q becomes 1.",
                     "Now S = 0, R = 0: the flip-flop holds Q = 1.",
                     "It remembered the pulse."));

        t.add(Topic.of("d-ff", SEQUENTIAL, "D Flip-Flop", "Copies the input D to the output Q on each clock edge")
            .theory(
                "The D (data or delay) flip-flop removes the forbidden state of the SR flip-flop. It has a single data input D: on every clock edge the output Q takes the value of D, and holds it until the next edge.",
                "Because it just remembers its input for one clock period, it is the standard building block of registers.")
            .points("Characteristic equation: Q(next) = D.", "No invalid input combination.",
                    "Output changes only on the clock edge.")
            .uses("Registers and memory cells.", "Delaying a signal by one clock cycle.")
            .example("Worked example: D = 1, clock pulse",
                     "Before the pulse Q may be 0.",
                     "On the clock edge Q copies D.",
                     "Q = 1 and stays 1 even if D changes before the next edge."));

        t.add(Topic.of("jk-ff", SEQUENTIAL, "JK Flip-Flop", "An SR flip-flop with no forbidden state: it can toggle")
            .theory(
                "The JK flip-flop fixes the SR problem. J acts like Set and K like Reset, but when J = K = 1 the output toggles (flips) on every clock pulse instead of being invalid.",
                "It is the most versatile flip-flop: it can hold, set, reset or toggle, so T and D flip-flops can both be made from it.")
            .points("J=0, K=0: hold.", "J=0, K=1: reset.", "J=1, K=0: set.", "J=1, K=1: toggle.",
                    "Q(next) = J·Q' + K'·Q.")
            .uses("Counters and frequency dividers.")
            .example("Worked example: J = K = 1, three clock pulses, Q starts at 0",
                     "Pulse 1: toggle, Q = 1.",
                     "Pulse 2: toggle, Q = 0.",
                     "Pulse 3: toggle, Q = 1.  The output frequency is half of the clock."));

        t.add(Topic.of("t-ff", SEQUENTIAL, "T Flip-Flop", "Toggles its output when T = 1")
            .theory(
                "The T (toggle) flip-flop has a single input T. If T = 0 the output holds; if T = 1 the output flips on each clock edge.",
                "It is a JK flip-flop with J and K tied together. Because it divides the clock frequency by 2, it is the basic element of binary counters.")
            .points("Q(next) = T ⊕ Q.", "T = 0: hold. T = 1: toggle.",
                    "Divides the clock frequency by 2.")
            .uses("Counters and frequency dividers.")
            .example("Worked example: T = 1, four pulses, Q starts at 0",
                     "Q sequence after each pulse: 1, 0, 1, 0.",
                     "Q completes one full cycle every two clock pulses.",
                     "So the output frequency is clock / 2."));

        t.add(Topic.of("ms-ff", SEQUENTIAL, "Master-Slave JK Flip-Flop", "Two latches in series to avoid the race-around problem")
            .theory(
                "A plain JK flip-flop with J = K = 1 can toggle many times while the clock stays high, because its output feeds back to its input. This is the race-around problem.",
                "The master-slave design uses TWO latches. While the clock is HIGH the master reads J and K. When the clock goes LOW the master is frozen and the slave copies the master. The output changes only once per clock cycle.")
            .points("Master is active when CLK = 1; slave is active when CLK = 0.",
                    "Output changes at the falling edge.", "Cures the race-around condition.")
            .uses("Reliable toggling in older counter designs.")
            .example("Worked example: J = K = 1, Q = 0",
                     "Clock HIGH: master toggles to 1, output Q is unchanged.",
                     "Clock LOW: slave copies master, Q = 1.",
                     "Exactly one toggle per full clock cycle."));

        t.add(Topic.of("ff-convert", SEQUENTIAL, "Flip-Flop Conversion", "Make one type of flip-flop behave like another")
            .theory(
                "Sometimes only one kind of flip-flop is available. By adding a little logic in front of its inputs you can make it behave like another type. The method uses the EXCITATION TABLE of the available flip-flop, which says what inputs are needed to move from the present state Q to the next state Q(next).",
                "For example, to make a JK flip-flop behave like a D flip-flop, J = D and K = D'. For a T flip-flop made from JK, J = K = T.")
            .points("Write the target flip-flop's truth table.", "Use the excitation table of the available flip-flop.",
                    "Derive input equations (with a K-map if needed).")
            .uses("Using whatever flip-flop a chip provides.")
            .example("Worked example: D flip-flop from JK",
                     "We want Q(next) = D.",
                     "Choose J = D and K = D'.",
                     "If D = 1: J=1, K=0 sets Q. If D = 0: J=0, K=1 resets Q."));

        t.add(Topic.of("counter", SEQUENTIAL, "Counters", "Flip-flops that count clock pulses")
            .theory(
                "A counter is a group of flip-flops that steps through a fixed sequence of states each time a clock pulse arrives. An n-bit binary counter counts from 0 to 2ⁿ - 1 and then wraps around to 0.",
                "In an ASYNCHRONOUS (ripple) counter each flip-flop is clocked by the previous one, so the changes ripple along with a small delay. In a SYNCHRONOUS counter all flip-flops share the same clock and change together, which is faster and glitch-free. An UP/DOWN counter can count in both directions, and a BCD (decade) counter counts 0 to 9 and then restarts.")
            .points("n flip-flops give 2ⁿ states (the 'modulus').", "Asynchronous: simple but slower (ripple delay).",
                    "Synchronous: all flip-flops switch together.", "BCD counter: modulus 10, resets after 9.")
            .uses("Timers and clocks.", "Frequency dividers.", "Program counters inside CPUs.")
            .example("Worked example: 3-bit up counter",
                     "States: 000, 001, 010, 011, 100, 101, 110, 111.",
                     "After 111 the next pulse wraps to 000.",
                     "It has 8 states, so it is a mod-8 counter."));

        t.add(Topic.of("shift-reg", SEQUENTIAL, "Shift Registers", "A row of flip-flops that moves data one place per clock pulse")
            .theory(
                "A shift register is a chain of flip-flops where each one passes its bit to the next on every clock pulse. Data can enter and leave either one bit at a time (serial) or all bits at once (parallel). That gives four types: SISO (serial in, serial out), SIPO (serial in, parallel out), PISO (parallel in, serial out) and PIPO (parallel in, parallel out).",
                "SIPO converts a serial stream into parallel data, and PISO does the opposite. They are the key to sending data over a single wire.")
            .points("Each clock pulse shifts every bit one position.", "SISO: delay line.",
                    "SIPO: serial to parallel.", "PISO: parallel to serial.", "PIPO: temporary storage.")
            .uses("Serial communication (UART, SPI).", "Delaying a signal by n clock cycles.")
            .example("Worked example: 4-bit SIPO, serial input 1, 0, 1, 1 (bits enter on the right and move left)",
                     "After pulse 1: 0001.  After pulse 2: 0010 (the 1 moved one place left, a new 0 entered).",
                     "After pulse 3: 0101.  After pulse 4: 1011.",
                     "The 4 serial bits are now available in parallel as 1011."));
        return t;
    }
}
