# 03. DSD Module Specification
**Project:** DSD + COA Interactive Virtual Laboratory  
**Module:** Digital System Design (DSD) Core Engine  
**Author:** Member 1  
**Status:** Completed & Tested  

---

## 1. Module Overview
The DSD Core Engine provides the backend logical, arithmetic, and algebraic evaluation layer for the virtual laboratory. It is entirely decoupled from the presentation layer (JavaFX) to ensure modularity, unit testability, and seamless integration with the sequential circuits, ALU, and circuit canvas.

---

## 2. Package Architecture

```text
src/main/java/dsd/
├── DsdService.java                  <- Primary integration facade
├── gates/                           <- Elementary and universal logic gates
│   ├── LogicGate.java (Interface)
│   ├── AndGate.java, OrGate.java, NotGate.java
│   └── NandGate.java, NorGate.java, XorGate.java, XnorGate.java
├── numbers/                         <- Number systems and code conversions
│   ├── ConversionResult.java
│   └── NumberConverter.java
├── combinational/                   <- Core and advanced combinational circuits
│   ├── CombinationalCircuit.java (Interface)
│   ├── HalfAdder.java, FullAdder.java
│   ├── HalfSubtractor.java, FullSubtractor.java
│   ├── RippleCarryAdderSubtractor.java
│   ├── AluArithmeticResult.java
│   ├── Multiplexer4to1.java, Decoder3to8.java
│   ├── PriorityEncoder8to3.java, MagnitudeComparator.java
│   └── BcdAdder.java
└── booleanlogic/                    <- Truth tables, parsing, and K-Maps
    ├── BooleanExpressionParser.java
    ├── TruthTable.java
    └── KMapSolver.java

