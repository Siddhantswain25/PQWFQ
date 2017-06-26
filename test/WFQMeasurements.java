import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import source.strategies.ExponentialPacketGenerationStrategy;
import components.*;
import components.System;


class WFQMeasurements {

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
    void wfq() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 4000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 4000);

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
    void wfq1() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 8100);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 900);

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
    void wfq2() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 900);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 8100);

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
    void wfq3() throws IllegalArgumentException {
        try {
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 2000);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 2000);

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