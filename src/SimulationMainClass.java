import source.ExponentialPacketGenerationStrategy;
import source.OnOffExpPacketGenerationStrategy;
import components.QueuePQWFQ;
import components.Statistics;
import components.System;

public class SimulationMainClass {
    public static void main(String[] args) {

        final int N = 70000000; //number of arrivals
        final double lambda_lp = 5; //arrival intensity for low priority queues
        final double lambda_hp = 1;
        final OnOffExpPacketGenerationStrategy onOffStrategy = new OnOffExpPacketGenerationStrategy(0.5, 1, 1000);
        final ExponentialPacketGenerationStrategy lpstrategy = new ExponentialPacketGenerationStrategy(lambda_lp);
        final ExponentialPacketGenerationStrategy hpstrategy = new ExponentialPacketGenerationStrategy(lambda_hp);
        final double C = 9000; //server service bitrate [b/s]

        System system = new System(C);
        try {
            system.addQueue(3, QueuePQWFQ.HIGH_PRIORITY, 1);
            //TODO: change weights to bitrate Ci
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.3);
            system.addQueue(1, QueuePQWFQ.LOW_PRIORITY, 0.3);

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
