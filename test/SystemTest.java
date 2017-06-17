import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import source.strategies.ExponentialPacketGenerationStrategy;
import components.*;
import components.System;


class SystemTest {

    private System system;
    private int N;

    @BeforeEach
    void setUp() {
        N = 5000;
    }

    @AfterEach
    void tearDown() {
        system = null;
        N = 0;
        Clock.reset();
    }

    @Test
    void oneHighPriorityQueue() throws IllegalArgumentException {
        final ExponentialPacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(1);
        final double C = 1000; //server service bitrate [b/s]

       system = new System(new Server(C));
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
            system.addSource(1, 0, 10, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

    @Test
    void twoLowPriorityQueues() throws IllegalArgumentException {
        final ExponentialPacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(5);
        final double C = 1000; //server service bitrate [b/s]

        system = new System(new Server(C));
        try {
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 1);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 1);
            system.addSource(1, 0, 10, strategy);
            system.addSource(2, 0, 10, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

    @Test
    void oneHighAndTwoLowPriorityQueues() throws IllegalArgumentException {
        final ExponentialPacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(1);
        final double C = 1000; //server service bitrate [b/s]

        System system = new System(new Server(C));
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.5);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 0.5);
            system.addSource(1, 0, 10, strategy);
            system.addSource(2, 0, 10, strategy);
            system.addSource(3, 0, 10, strategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }

}