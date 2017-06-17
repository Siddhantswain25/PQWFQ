import source.strategies.ExponentialPacketGenerationStrategy;
import source.strategies.OnOffExpPacketGenerationStrategy;
import components.QueuePQWFQ;
import components.Statistics;
import components.System;

public class SimulationMainClass {
    public static void main(String[] args) {
        //TODO: move it to configuration file?
        final int N = 70000000; //number of arrivals

        final OnOffExpPacketGenerationStrategy onOffStrategy = new OnOffExpPacketGenerationStrategy(0.5, 1, 1000);
        final ExponentialPacketGenerationStrategy lpstrategy = new ExponentialPacketGenerationStrategy(5);
        final ExponentialPacketGenerationStrategy hpstrategy = new ExponentialPacketGenerationStrategy(1);

        final double C = 9000; //server service bitrate [b/s]

        System system = new System(C);
        try {
            system.addQueue(3, QueuePQWFQ.HIGH_PRIORITY, C);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 4500);
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 4500);

            system.addSource(3, 0, 100, hpstrategy);
            system.addSource(2, 0, 100, lpstrategy);
            system.addSource(1, 0, 100, lpstrategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
}
