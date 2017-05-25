public class SimulationMainClass {
    public static void main(String[] args) {

        final int N = 70000000;     //number of arrivals
        final double lambda = 0.1;  //arrival intensity
        final double mi = 10;       //do i even need this?
        final double C = 1000;      //server service bitrate [b/s]

        Server server = new Server(C);
        try {
            server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1, 20);
            server.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 0.5, 50);
            server.addQueue(3, QueuePQWFQ.LOW_PRIORITY, 0.5, 70);
        } catch (InvalidQueueParametersException e) { e.printStackTrace(); }

        System system = new System(server, lambda, mi);
        //system.setStrategy(new PoissonPacketGenerationStrategy(lambda)); //if there is a need to change default exp

        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
        }
        system.displayAllStatistics();
    }
}
