public class SimulationMainClass {
    public static void main(String[] args) {

        final int N = 700000; //number of arrivals
        final double lambda_lp = 10; //arrival intensity for low priority queues
        final double lambda_hp = 10;
        final OnOffExpPacketGenerationStrategy onOffStrategy = new OnOffExpPacketGenerationStrategy(1, 0.5, 1000, 10);
        final ExponentialPacketGenerationStrategy lpstrategy = new ExponentialPacketGenerationStrategy(lambda_lp);
        final ExponentialPacketGenerationStrategy hpstrategy = new ExponentialPacketGenerationStrategy(lambda_hp);
        final double C = 8000; //server service bitrate [b/s]

        System system = new System(new Server(C));
        try {
            system.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
            system.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.5);
            system.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 0.5);
            system.addSource(1, 0, 10, onOffStrategy);
            system.addSource(2, 0, 10, lpstrategy);
            system.addSource(3, 0, 10, lpstrategy);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
}
