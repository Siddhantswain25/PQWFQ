public class System {
    private double lambda;
    private double mi;

    private double totalServiceTime;
    private int numberOfArrivals;
    private double sumOfArrivalIntervals;

    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;

    private EventList eventList;
    private Server server;

    System() {
        this(1.0, 10.0);
    }

    System(double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
        initialize();
    }

    System(Server server, double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        resetAllStatistics();
        eventList = new EventList();
        this.server = server;
        initialize();
    }

    private void initialize() {
        server.forEachQueue((id, queue) -> scheduleNextArrival(id));
    }
    //TODO: does it count as start of all sources in the same time?

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
            scheduleNextDeparture();
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
            scheduleNextDeparture();
        }
    }

    private void scheduleNextArrival(int queueId) {
        double timeToNextArrival = RandomGenerator.getExpRandom(lambda);
        sumOfArrivalIntervals += timeToNextArrival;
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, queueId));
    }

    private void scheduleNextDeparture(){
        double serviceTime = RandomGenerator.getExpRandom(mi);
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
        double totalSimTime = Clock.getCurrentTime();

        double expectedRho = lambda/mi;
        double expectedW = (expectedRho/mi)/(1-expectedRho);

        double dn = totalDelay/numberOfDelays;
        double qn = queueTime/totalSimTime;
        double un = serverBusyTime/totalSimTime;

        double avgServiceTime = totalServiceTime/numberOfDelays;
        double avgArrivalInterval = sumOfArrivalIntervals/numberOfArrivals;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
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
        java.lang.System.out.println("d(n):\t" + dn + "  (avg waiting time)");
        java.lang.System.out.println("q(n):\t" + qn + "  (avg queue size)");
        java.lang.System.out.println("u(n):\t" + un + "   (avg system load)");
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
