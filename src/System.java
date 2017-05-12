public class System {
    private double lambda;
    private double mu;
    private int numberOfDelays;
    private double totalDelay;
    private double queueTime;
    private double serverBusyTime;

    private EventList eventList;
    private Server server;

    System() {
        lambda = 1.0;
        mu = 1.0;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
    }

    System(double lambda, double mu) {
        this.lambda = lambda;
        this.mu = mu;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
    }

    public void addEvent(Event event) {
        eventList.addEvent(event);
    }

    public void processNextEvent() {
        Event event = eventList.popNextEvent();
        if (event.getEventType() == EventType.ARRIVAL)
            processArrival();
        else
            processDeparture();
    }

    private void processArrival() {
        if(server.isBusy()) {
            server.addClient(Clock.getCurrentTime());
        }
        else {
            server.setIsBusy(true);
            //stats
            addEvent(new Event(EventType.DEPARTURE, RandomGenerator.getPoissonRandom(1.0)));
        }
    }

    private void processDeparture() {

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

    public void displayAllStatistics() {
        double expectedW = 1.0/(mu - lambda);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Number of delays/serviced customers: " + numberOfDelays);
        java.lang.System.out.println("Total delay: " + totalDelay);
        java.lang.System.out.println("Q(t): " + queueTime);
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("Expected W: " + expectedW);
        java.lang.System.out.println("-----------------------------------------------");
    }

}
