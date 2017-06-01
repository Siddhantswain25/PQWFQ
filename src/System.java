import java.util.HashMap;
import java.util.Random;

public class System {
    private EventList eventList;
    private Server server;
    private Statistics statistics;

    System(Server server) {
        eventList = new EventList();
        this.server = server;
        statistics = new Statistics(server.getSetOfQueueIds());
        initialize();
    }

    private void initialize() {
        server.forEachQueue((id, queue) -> scheduleNextArrival(id));
        //TODO: start each source independently
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
        statistics.increaseNumberOfArrivals();
        int queueId = event.getQueueId();
        scheduleNextArrival(queueId);
        Packet packet = server.wfqArrivalAlgorithm(event);

        if(server.isBusy()) {
            server.addClient(queueId, packet);
        } else {
            server.setIsBusy(true);
            server.setSpacingTime(packet.getVirtualSpacingTimestamp());
            addDelayToStatistics(queueId, 0.0);
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
            addDelayToStatistics(queueId, waitingTime);
            scheduleNextDeparture(handledPacket.getSize());
        }
    }

    private void scheduleNextArrival(int queueId) {
        double timeToNextArrival = server.getNextArrivalTime(queueId);
        statistics.increaseSumOfArrivalIntervals(timeToNextArrival);
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, queueId));
    }

    private void scheduleNextDeparture(int packetSizeInBytes){
        double serviceTime = (packetSizeInBytes*8)/server.getServiceBitrate();
        statistics.increaseTotalServiceTime(serviceTime);
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    private void updateStatistics(double timeDelta) {
        if(isServerBusy())
            statistics.increaseServerBusyTime(timeDelta);

        server.forEachQueue((id, queue) -> statistics.increaseQueueTime(id,queue.size() * timeDelta));
    }

    private void addDelayToStatistics(int queueId, double delay) {
        statistics.increaseTotalDelay(queueId, delay);
        statistics.increaseNumberOfDelays(queueId);
    }

    private boolean isServerBusy() {
        return server.isBusy();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public long getNumberOfArrivals() {
        return statistics.getNumberOfArrivals();
    }

    public void displayTrace() {
        double simTime = Clock.getCurrentTime();
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Current time: " + simTime);
        java.lang.System.out.println("B(t): " + statistics.getServerBusyTime());
        java.lang.System.out.println("Is server busy?: " + isServerBusy());
        server.forEachQueue((id, queue) -> {
            java.lang.System.out.println("-----------------------------------------------");
            java.lang.System.out.println("QUEUE " + id);
            Statistics.QueueStatistics stats = statistics.getQueueStatistics(id);
            java.lang.System.out.println("Number of delays: " + stats.numberOfDelays);
            java.lang.System.out.println("Total delay: " + stats.totalDelay);
            java.lang.System.out.println("Q(t): " + stats.queueTime);
            java.lang.System.out.print("Clients in queue " + id + ": " + server.getQueueSize(id));
            java.lang.System.out.println(" (priority: " + queue.getPriority() + ")");
            if(!queue.isEmpty())
                java.lang.System.out.println("Lowest timestamp: " + queue.peekLowestTimestamp());
        });
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("Source number: " + eventList.peekNextEvent().getQueueId());
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println();
    }
}
