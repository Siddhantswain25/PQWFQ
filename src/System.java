public class System {
    private double lambda;
    private double mi;
    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;
    private double serverFreeTime;
    private int timesServerWasNotBusy;

    private EventList eventList;
    private Server server;


    System() {
        lambda = 1.0;
        mi = 10;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        initialize();
    }

    System(double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
        initialize();
    }

    private void initialize() {
        scheduleNextArrival();
    }

    private void addEvent(Event event) {
        eventList.addEvent(event);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();

        if(event != null) {
            double previousTime = Clock.getCurrentTime();
            Clock.setTime(event.getTime());
            double timeDelta = Clock.getCurrentTime() - previousTime;

            updateStatistics(timeDelta);

            if (event.getEventType() == EventType.ARRIVAL)
                processArrival();
            else
                processDeparture();
        } else {
            java.lang.System.out.println("Allarme! Event was NULL!");
        }
    }

    private void updateStatistics(double timeDelta) {
        if(server.isBusy())
            serverBusyTime += timeDelta;
        else
            serverFreeTime += timeDelta;
        queueTime += server.getQueueSize() * timeDelta;
    }

    private void processArrival() {
        scheduleNextArrival();
        if(server.isBusy()) {
            server.addClient(Clock.getCurrentTime());
        }
        else {
            timesServerWasNotBusy += 1;
            server.setIsBusy(true);
            addDelayToStatistics(0.0);
            scheduleNextDeparture();
        }
    }

    private void processDeparture() {
        if(server.isQueueEmpty()) {
            server.setIsBusy(false);
        } else {
            double waitingTime = Clock.getCurrentTime() - server.handleNextClient();
            addDelayToStatistics(waitingTime);
            scheduleNextDeparture();
        }
    }

    private void scheduleNextArrival() {
        double nextArrivalTime = Clock.getCurrentTime() + RandomGenerator.getPoissonRandom(lambda);
        //double nextArrivalTime = Clock.getCurrentTime() + RandomGenerator.getExpRandom(lambda);
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime));
    }

    private void scheduleNextDeparture(){
        //double nextDepartureTime = Clock.getCurrentTime() + RandomGenerator.getPoissonRandom(mi);
        double nextDepartureTime = Clock.getCurrentTime() + RandomGenerator.getExpRandom(1/mi);
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    public boolean isEventListEmpty() {
        return eventList.isEmpty();
    }

    public boolean isServerBusy() {
        return server.isBusy();
    }

    public void resetAllStatistics() {
        numberOfDelays = 0;
        totalDelay = 0.0;
        queueTime = 0.0;
        serverBusyTime = 0.0;
    }

    private void addDelayToStatistics(double delay) {
        totalDelay += delay;
        numberOfDelays++;
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        double expectedRho = lambda/mi;
        double expectedW = (expectedRho/mi)/(1-expectedRho);

        double dn = totalDelay/numberOfDelays;
        double qn = queueTime/totalSimTime;
        double un = serverBusyTime/totalSimTime;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Total simulation time: " + totalSimTime);
        java.lang.System.out.println("Number of delays/serviced customers: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        //java.lang.System.out.println("!B(t): " + serverFreeTime);
        //java.lang.System.out.println("Times server wasn't busy when client arrived: " + timesServerWasNotBusy);
        //java.lang.System.out.println("Clients left in queue: " + server.getQueueSize());
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Rho - average system load\nW - average waiting time");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Expected Rho:\t" + expectedRho);
        java.lang.System.out.println("Expected W:\t\t" + expectedW);
        java.lang.System.out.println("d(n}:\t" + dn);
        java.lang.System.out.println("q(n}:\t" + qn);
        java.lang.System.out.println("u(n}:\t" + un);
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
        java.lang.System.out.println("Times server wasn't busy: " + timesServerWasNotBusy);
        java.lang.System.out.println("Is server busy?: " + isServerBusy());
        java.lang.System.out.println("Clients in queue: " + server.getQueueSize());
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("-----------------------------------------------");
    }
}
