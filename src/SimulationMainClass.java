public class SimulationMainClass {
    public static void main(String[] args) {

        final int N = 700000; //number of arrivals
        final double lambda_lq = 1; //arrival intensity for low priority queues
        final double lambda_hq = 0.1;
        final double C = 1000; //server service bitrate [b/s]

        Server server = new Server(C, lambda_lq);
        try {
            server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1, 20, lambda_hq);
            server.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.25, 50, lambda_lq);
            server.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 0.75, 50, lambda_lq);
        } catch (IllegalArgumentException e) { e.printStackTrace(); }

        System system = new System(server);
        //system.setStrategy(new PoissonPacketGenerationStrategy(lambda)); //if there is a need to change default exp

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }

        Statistics statistics = system.getStatistics();
        statistics.displayAllStatistics();
    }
}
