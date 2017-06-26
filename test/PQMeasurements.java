import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import source.strategies.ExponentialPacketGenerationStrategy;
import components.*;
import components.System;


class PQMeasurements {

    private System system;
    private int N;
    private final double C = 9000; //server service bitrate [b/s]
    private final ExponentialPacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(5);

    @BeforeEach
    void setUp() {
        N = 70000000;
        system = new System(new Server(C));
    }

    @AfterEach
    void tearDown() {
        system = null;
        N = 0;
        Clock.reset();
    }

    @Test
    void pq1() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 9000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 9000);

            system.addSource(1, 0, 100, strategy);
            system.addSource(2, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
    @Test

    void pq2() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 4500);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 4500);

            system.addSource(1, 0, 100, strategy);
            system.addSource(2, 0, 100, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
}