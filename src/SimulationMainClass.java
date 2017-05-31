public class SimulationMainClass {
    public static void main(String[] args) {

        final int N = 700000;     //number of arrivals
        final double lambda = 0.1;  //arrival intensity
        final double C = 1000;      //server service bitrate [b/s]
        final PacketGenerationStrategy defaultStrategy = new ExponentialPacketGenerationStrategy(1/lambda);

        Server server = new Server(C, defaultStrategy);
        try {
            server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1, 20);
            server.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.5, 50);
            server.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 0.5, 70);
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
