public class System {
    private double lambda;
    private double mi;
    //TODO: delete them and move ALL statistics to separate class made only for this purpose

    private double totalServiceTime;
    private int numberOfArrivals;
    private double sumOfArrivalIntervals;

    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;

    private EventList eventList;
    private Server server;
    private PacketGenerationStrategy strategy;

    System(Server server, double lambda, double mi) {
        resetAllStatistics();
        eventList = new EventList();
        this.server = server;

        //TODO: read TODO next to the parameters declaration and remove them from constructor
        this.lambda = lambda;
        this.mi = mi;

        setStrategy(new ExponentialPacketGenerationStrategy(1/lambda));
        initialize();
    }

    private void initialize() {
        server.forEachQueue((id, queue) -> scheduleNextArrival(id));
        //TODO: start each source independently
    }

    public void setStrategy(PacketGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    private void addEvent(Event event) {
        eventList.addEvent(event);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();

        double previousTime = Clock.getCurrentTime();
        Clock.setTime(event.getTime());
        double timeDelta = Clock.getCurrentTime() - previousTime;

        updateStatistics(timeDelta);

        if (event.getEventType() == EventType.ARRIVAL)
            processArrival(event);
        else
            processDeparture();
    }

    private void processArrival(Event event) {
        numberOfArrivals++;
        scheduleNextArrival(event.getQueueId());
        Packet packet = server.wfqArrivalAlgorithm(event);

        if(server.isBusy()) {
            server.addClient(event.getQueueId(), packet);
        } else {
            server.setIsBusy(true);
            server.setSpacingTime(packet.getVirtualSpacingTimestamp());
            addDelayToStatistics(0.0);
            scheduleNextDeparture(packet.getSize());
        }
    }

    private void processDeparture() {
        if(server.areAllQueuesEmpty()) {
            server.setIsBusy(false);
        } else {
            int queueId = server.pqwfqDepartureAlgorithm();
            Packet handledPacket = server.handleNextClient(queueId);

            double clientArrivalTime = handledPacket.getArrivalTime();
            double waitingTime = Clock.getCurrentTime() - clientArrivalTime;

            server.setSpacingTime(handledPacket.getVirtualSpacingTimestamp());
            addDelayToStatistics(waitingTime);
            scheduleNextDeparture(handledPacket.getSize());
        }
    }

    private void scheduleNextArrival(int queueId) {
        double timeToNextArrival = strategy.getTimeToNextArrival();
        sumOfArrivalIntervals += timeToNextArrival;
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, queueId));
    }

    private void scheduleNextDeparture(int packetSizeInBytes){
        double serviceTime = (packetSizeInBytes*8)/server.getServiceBitrate();
        totalServiceTime += serviceTime;
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    private void updateStatistics(double timeDelta) {
        if(isServerBusy())
            serverBusyTime += timeDelta;

        server.forEachQueue((id, queue) -> queueTime += queue.size() * timeDelta);
    }

    private void addDelayToStatistics(double delay) {
        totalDelay += delay;
        numberOfDelays++;
    }

    private boolean isServerBusy() {
        return server.isBusy();
    }

    public int getNumberOfArrivals() {
        return numberOfArrivals;
    }

    public void resetAllStatistics() {
        totalServiceTime = 0.0;
        numberOfArrivals = 0;
        numberOfDelays = 0;
        totalDelay = 0.0;
        queueTime = 0.0;
        serverBusyTime = 0.0;
    }

    public void displayAllStatistics() {
        //TODO: move statistics methods to a new class

        double totalSimTime = Clock.getCurrentTime();

        //TODO: numberOfQueues = 1 -> display MM1 stats, number > 1, hasPriorityQueue -> display pqwfq stats
        double expectedRho = lambda/mi;
        double expectedW = (expectedRho/mi)/(1-expectedRho);

        double dn = totalDelay/numberOfDelays; //TODO: for each queue
        double qn = queueTime/totalSimTime;
        double un = serverBusyTime/totalSimTime; //TODO: total load, and for each queue
        //TODO: avg queue size for each queue

        double avgServiceTime = totalServiceTime/numberOfDelays;
        double avgArrivalInterval = sumOfArrivalIntervals/numberOfArrivals;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("-------- WARNING! STATS ARE DEPRECATED --------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("lambda:\t" + lambda + "\nmi:\t" + mi);
        java.lang.System.out.println("Total simulation time: " + totalSimTime);
        java.lang.System.out.println("Number of arrivals: " + numberOfArrivals);
        java.lang.System.out.println("Number of delays/serviced customers: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Rho - average system load\nW - average waiting time");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Expected Rho:\t" + expectedRho);
        java.lang.System.out.println("Expected W:\t\t" + expectedW);
        java.lang.System.out.println("Actual W:\t" + dn + "  (d(n) avg waiting time)");
        java.lang.System.out.println("Average queue size:\t" + qn + "  (q(n))");
        java.lang.System.out.println("Actual Rho:\t" + un + "   (u(n) avg system load)");
        java.lang.System.out.println("avgServiceTime:\t\t" + avgServiceTime);
        java.lang.System.out.println("avgArrivalInterval:\t" + avgArrivalInterval);
        java.lang.System.out.println("-----------------------------------------------");
    }

    public void displayTrace() {
        double totalSimTime = Clock.getCurrentTime();
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Current time: " + totalSimTime);
        java.lang.System.out.println("Number of delays: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("Is server busy?: " + isServerBusy());
        server.forEachQueue((id, queue) -> {
            java.lang.System.out.print("Clients in queue " + id + ": " + server.getQueueSize(id));
            java.lang.System.out.println(" (priority: " + queue.getPriority() + ")");
            if(!queue.isEmpty())
                java.lang.System.out.println("Lowest timestamp: " + queue.peekLowestTimestamp());
        });
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("Source number: " + eventList.peekNextEvent().getQueueId());
        java.lang.System.out.println("-----------------------------------------------");
    }
}
