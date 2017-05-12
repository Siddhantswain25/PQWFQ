public class System {
    private double lambda;
    private double mi;
    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;

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

    public void addEvent(Event event) {
        eventList.addEvent(event);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();
        if(event != null) {
            double previousTime = Clock.getCurrentTime();
            Clock.incrementTime(event.getTime());
            double timeDelta = Clock.getCurrentTime() - previousTime;

            if(server.isBusy())
                serverBusyTime += timeDelta;

            queueTime += server.getQueueSize() * timeDelta;

            if (event.getEventType() == EventType.ARRIVAL)
                processArrival();
            else
                processDeparture();
        }
    }

    private void processArrival() {
        scheduleNextArrival();
        if(server.isBusy()) {
            server.addClient(Clock.getCurrentTime());
        }
        else {
            server.setIsBusy(true);
            addDelayToStatistics(0.0);
            scheduleNextDeparture();
        }
    }

    private void processDeparture() {
        if(server.isQueueEmpty()) {
            server.setIsBusy(false);
        } else {
            double delay = Clock.getCurrentTime() - server.handleNextClient();
            addDelayToStatistics(delay);
            scheduleNextDeparture();
        }
    }

    private void scheduleNextArrival() {
        double nextArrivalTime = Clock.getCurrentTime() + RandomGenerator.getExpRandom(lambda);
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime));
    }

    private void scheduleNextDeparture(){
        double nextDepartureTime = Clock.getCurrentTime() + RandomGenerator.getPoissonRandom(mi);
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    public boolean isEventListEmpty() {
        return eventList.isEmpty();
    }

    public EventType getNextEventType() {
        return eventList.peekNextEvent().getEventType();
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

        double expectedRho = lambda/ mi;
        double actualRho = serverBusyTime/totalSimTime;

        double expectedW = 1.0/(mi - lambda);
        double expectedW2 = (expectedRho/ mi)/(1-expectedRho);
        double actualW = totalDelay/totalSimTime;


        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Number of delays/serviced customers: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Rho - average system load");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Expected Rho:\t" + expectedRho);
        java.lang.System.out.println("Actual Rho:\t\t" + actualRho);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("W - average waiting time");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Expected W:\t" + expectedW);
        java.lang.System.out.println("Expected W:\t" + expectedW2 + "\t(formula from lecture)");
        java.lang.System.out.println("Actual W:\t" + actualW);
        java.lang.System.out.println("-----------------------------------------------");
    }

}
