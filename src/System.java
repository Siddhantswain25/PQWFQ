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
        mi = 1.0;
        resetAllStatistics();
        eventList = new EventList();
        server = new Server();
    }

    System(double lambda, double mi) {
        this.lambda = lambda;
        this.mi = mi;
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
            addDelayToStatistics(0.0);
            addEvent(new Event(EventType.DEPARTURE, RandomGenerator.getPoissonRandom(mi)));
        }
    }

    private void processDeparture() {
        if(server.isQueueEmpty()) {
            server.setIsBusy(false);
        } else {
            Event event = eventList.popNextEvent();
            double delay = Clock.getCurrentTime() - event.getTime();
            addDelayToStatistics(delay);
            addEvent(new Event(EventType.DEPARTURE, RandomGenerator.getPoissonRandom(mi)));
        }
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
        java.lang.System.out.println("Total delay:\t" + totalDelay);
        java.lang.System.out.println("Q(t):\t" + queueTime);
        java.lang.System.out.println("B(t):\t" + serverBusyTime);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Rho - average system load");
        java.lang.System.out.println("Expected Rho:\t" + expectedRho);
        java.lang.System.out.println("Actual Rho:\t" + actualRho);
        java.lang.System.out.println("W - average waiting time");
        java.lang.System.out.println("Expected W:\t" + expectedW);
        java.lang.System.out.println("Expected W:\t" + expectedW2 + "\t(formula from lecture)");
        java.lang.System.out.println("Actual W:\t" + actualW);
        java.lang.System.out.println("-----------------------------------------------");
    }

}
