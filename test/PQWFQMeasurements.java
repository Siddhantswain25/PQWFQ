import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import source.strategies.ExponentialPacketGenerationStrategy;
import components.*;
import components.System;


class PQWFQMeasurements {

    private System system;
    private int N = 80000000;
    private final double C = 9000; //server service bitrate [b/s]
    private final ExponentialPacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(5);
    private final ExponentialPacketGenerationStrategy hqstrategy = new ExponentialPacketGenerationStrategy(1);

    @BeforeEach
    void setUp() {
        system = new System(new Server(C));
    }

    @AfterEach
    void tearDown() {
        system = null;
        N = 0;
        Clock.reset();
    }

    @Test
    void pqwfq1() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 9000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 4500);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 4500);

            system.addSource(1, 0, 100, hqstrategy);
            system.addSource(2, 0, 100, strategy);
            system.addSource(3, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

    @Test
    void pqwfq2() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 9000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 8100);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 900);

            system.addSource(1, 0, 100, hqstrategy);
            system.addSource(2, 0, 100, strategy);
            system.addSource(3, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

    @Test
    void pqwfq3() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 8100);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 900);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 1100);

            system.addSource(1, 0, 100, hqstrategy);
            system.addSource(2, 0, 100, strategy);
            system.addSource(3, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

    @Test
    void pqwfq4() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 9000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 2700);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 2700);

            system.addSource(1, 0, 100, hqstrategy);
            system.addSource(2, 0, 100, strategy);
            system.addSource(3, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
}
