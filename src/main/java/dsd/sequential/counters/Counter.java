package dsd.sequential.counters;

/** Common contract for the counter implementations in this package. */
public interface Counter {

    void clockTick();

    void reset();

    int getValue();

    int getBitWidth();

    CounterState getState();

    String getName();
}
