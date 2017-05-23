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

    private double spacingTime; //TODO: move to Server class
    private double packetSize;

    private EventList eventList;
    private Server server;

    System() {
        this(1.0, 10.0);
    }

    System(double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        this.packetSize = 5; //TODO: change it
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
        initialize();
    }

    System(Server server, double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        this.packetSize = 5; //TODO: change it
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

    private int pqwfqAlgorithm() {
        //TODO: add PQ part
        return 1;
    }

    private void wfqArrivalAlgorithm(Event event) {
        int id = event.getQueueId();
        double vst = server.getQueue(id).getVirtualSpacingTimestamp();
        double ri = server.getQueue(id).getConnectionSpeed();

        double timestamp = Math.max(spacingTime, vst) + (packetSize/ri);
        server.getQueue(id).setVirtualSpacingTimestamp(timestamp);
        //server.addClient(id, new Packet(Clock.getCurrentTime(), timestamp));
        server.addClient(id, new Packet(timestamp)); //TODO: do it like this, or explicitly like above?
    }

    private int wfqDepartureAlgorithm() {
        return server.getIdOfQueueWithTheLowestTimestampOfNextPacket();
    }

    private void processArrival(Event event) {
        numberOfArrivals++;
        assert(event.getQueueId() != -1); //TODO: check if this is possible
        scheduleNextArrival(event.getQueueId());
        if(server.isBusy()) {
            wfqArrivalAlgorithm(event);
        }
        else {
            server.setIsBusy(true);
            addDelayToStatistics(0.0);
            scheduleNextDeparture();
        }
    }

    private void processDeparture() {
        if(server.areAllQueuesEmpty()) {
            server.setIsBusy(false);
        } else {
            int queueId = wfqDepartureAlgorithm(); //TODO: add PQ algorithm
            Packet handledPacket = server.handleNextClient(queueId);

            double clientArrivalTime = handledPacket.getArrivalTime();
            double waitingTime = Clock.getCurrentTime() - clientArrivalTime;

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

    public boolean isEventListEmpty() {
        return eventList.isEmpty();
    }

    public boolean isServerBusy() {
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
        spacingTime = 0.0;
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
        java.lang.System.out.println("Clients in queue: " + server.getQueueSize(1)); //TODO: do it for each
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("-----------------------------------------------");
    }
}
