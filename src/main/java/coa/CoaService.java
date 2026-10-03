package coa;

import coa.alu.Alu;
import coa.alu.AluOperation;
import coa.alu.AluResult;
import coa.arithmetic.BitUtils;
import coa.arithmetic.BoothMultiplicationResult;
import coa.arithmetic.BoothMultiplier;
import coa.arithmetic.DivisionResult;
import coa.arithmetic.NonRestoringDivision;
import coa.arithmetic.RestoringDivision;
import coa.cpu.Accumulator;
import coa.cpu.InstructionRegister;
import coa.cpu.ProgramCounter;
import coa.cpu.Register;
import coa.fpu.Ieee754Converter;
import coa.fpu.Ieee754Result;
import coa.memory.Cache;
import coa.memory.CacheAccessResult;
import coa.memory.Memory;

/**
 * Facade for the Computer Organization &amp; Architecture (COA) portion of
 * the Virtual Lab, analogous to {@code dsd.DsdService}: thin delegation
 * methods grouped by section, with all real logic living in the
 * implementation classes under {@code coa.*}.
 */
public class CoaService {

    private final Alu alu = new Alu();

    // --- Arithmetic (Booth's Multiplication, Restoring/Non-Restoring Division) ---

    public BoothMultiplicationResult computeBoothMultiplication(long multiplicand, long multiplier) {
        return BoothMultiplier.multiply(multiplicand, multiplier);
    }

    public BoothMultiplicationResult computeBoothMultiplication(long multiplicand, long multiplier, int bitWidth) {
        return BoothMultiplier.multiply(multiplicand, multiplier, bitWidth);
    }

    public DivisionResult computeRestoringDivision(long dividend, long divisor) {
        return RestoringDivision.divide(dividend, divisor);
    }

    public DivisionResult computeRestoringDivision(long dividend, long divisor, int bitWidth) {
        return RestoringDivision.divide(dividend, divisor, bitWidth);
    }

    public DivisionResult computeNonRestoringDivision(long dividend, long divisor) {
        return NonRestoringDivision.divide(dividend, divisor);
    }

    public DivisionResult computeNonRestoringDivision(long dividend, long divisor, int bitWidth) {
        return NonRestoringDivision.divide(dividend, divisor, bitWidth);
    }

    // --- IEEE 754 Floating Point ---

    public Ieee754Result convertToIeee754(double value) {
        return Ieee754Converter.toIeee754(value);
    }

    public Ieee754Result convertFromIeee754(int bitPattern) {
        return Ieee754Converter.fromIeee754(bitPattern);
    }

    // --- ALU Operations ---

    public AluResult computeAluOperation(boolean[] a, boolean[] b, AluOperation operation) {
        return alu.compute(a, b, operation);
    }

    public AluResult computeAluOperation(boolean[] a, AluOperation operation) {
        return alu.compute(a, operation);
    }

    public AluResult computeAluOperation(long a, long b, AluOperation operation, int bitWidth) {
        return alu.compute(BitUtils.toBits(a, bitWidth), BitUtils.toBits(b, bitWidth), operation);
    }

    public AluResult computeAluNot(long a, int bitWidth) {
        return alu.compute(BitUtils.toBits(a, bitWidth), AluOperation.NOT);
    }

    // --- CPU Registers ---

    public Register createRegister(String name, int width) {
        return new Register(name, width);
    }

    public Accumulator createAccumulator(int width) {
        return new Accumulator(width);
    }

    public ProgramCounter createProgramCounter(int width) {
        return new ProgramCounter(width);
    }

    public InstructionRegister createInstructionRegister(int width) {
        return new InstructionRegister(width);
    }

    // --- Memory ---

    public Memory createMemory(int capacity) {
        return new Memory(capacity);
    }

    public void writeMemory(Memory memory, int address, long value) {
        memory.write(address, value);
    }

    public long readMemory(Memory memory, int address) {
        return memory.read(address);
    }

    // --- Cache ---

    public Cache createCache(int numLines, int lineSizeBytes) {
        return new Cache(numLines, lineSizeBytes);
    }

    public CacheAccessResult accessCache(Cache cache, int address) {
        return cache.access(address);
    }
}
