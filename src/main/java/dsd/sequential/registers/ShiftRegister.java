package dsd.sequential.registers;

/** Common contract for the shift register implementations in this package. */
public interface ShiftRegister {

    void reset();

    int getBitWidth();

    ShiftRegisterState getState();

    String getName();
}
