package components;

import source.*;
import events.*;
import java.util.*;

public class System {
    private EventList eventList;
    private Server server;
    private HashMap<Integer, QueuePQWFQ> queues;
    private HashMap<Integer, Source> sources;
    private Statistics statistics;

    public System(double serverBitrate) {
        this(new Server(serverBitrate));
    }

    public System(Server server) {
        eventList = new EventList();
        queues = new HashMap<>();
        sources = new HashMap<>();
        this.server = server;
        statistics = new Statistics();
    }

    public void addQueue(int queueId, int priority, double weight) throws IllegalArgumentException {
        addQueue(queueId, new QueuePQWFQ(priority, weight));
    }

    public void addQueue(int queueId, QueuePQWFQ queue) throws IllegalArgumentException {
        if(queue.getPriority() == QueuePQWFQ.HIGH_PRIORITY && hasHighPriorityQueue())
            throw new IllegalArgumentException("System already has a high priority queue!");
        else if(queues.containsKey(queueId))
            throw new IllegalArgumentException("Queue with this ID already exists!");
        else {
            queues.put(queueId, queue);
            statistics.registerQueue(queueId);
        }
    }

    public void addSource(int sourceId, double startTime, int packetSizeInBytes, PacketGenerationStrategy strategy)
            throws IllegalArgumentException {
        if(sources.containsKey(sourceId))
            throw new IllegalArgumentException("Source with this ID already exists!");
        else {
            sources.put(sourceId, new Source(startTime, packetSizeInBytes, strategy));
            scheduleNextArrival(sourceId);
        }
    }

    private void addEvent(Event event) {
        eventList.addEvent(event);
    }

    private void addClient(int queueId, Packet packet) {
        queues.get(queueId).add(packet);
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
        Packet packet = wfqArrivalAlgorithm(event);

        if(server.isBusy()) {
            addClient(queueId, packet);
        } else {
            server.setIsBusy(true);
            server.setSpacingTime(packet.getVirtualSpacingTimestamp());
            statistics.addDelayToStatistics(queueId, 0.0);
            scheduleNextDeparture(packet.getSize());
        }
    }

    private void processDeparture() {
        if(areAllQueuesEmpty()) {
            server.setIsBusy(false);
        } else {
            int queueId = pqwfqDepartureAlgorithm();
            Packet handledPacket = handleNextClient(queueId);

            double clientArrivalTime = handledPacket.getArrivalTime();
            double waitingTime = Clock.getCurrentTime() - clientArrivalTime;

            server.setSpacingTime(handledPacket.getVirtualSpacingTimestamp());
            statistics.addDelayToStatistics(queueId, waitingTime);
            scheduleNextDeparture(handledPacket.getSize());
        }
    }

    private void scheduleNextArrival(int id) {
        double timeToNextArrival = sources.get(id).getTimeToNextArrival();
        statistics.increaseQueueArrivalInterval(id, timeToNextArrival);
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, id));
    }

    private void scheduleNextDeparture(int packetSizeInBytes){
        double serviceTime = (packetSizeInBytes*8)/server.getServiceBitrate();
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    private Packet handleNextClient(int queueId) {
        return queues.get(queueId).poll();
    }

    private int pqwfqDepartureAlgorithm() {
        if(hasHighPriorityQueue()) {
            int id;
            try {
                id = getHighPriorityQueueId();
                if(!queues.get(id).isEmpty())
                    return id;
            } catch(NoSuchElementException e) {
                return getIdOfQueueWithTheLowestTimestampOfNextPacket();
            }
        }
        return getIdOfQueueWithTheLowestTimestampOfNextPacket();
    }

    private int getIdOfQueueWithTheLowestTimestampOfNextPacket() {
        double lowestValue = Double.POSITIVE_INFINITY;
        ArrayList<Integer> id = new ArrayList<>();

        for(Map.Entry<Integer, QueuePQWFQ> entry : queues.entrySet()) {
            if(!entry.getValue().isEmpty() && entry.getValue().getPriority() == QueuePQWFQ.LOW_PRIORITY) {
                double entryLowestTimestamp = entry.getValue().peekLowestTimestamp();

                //TODO: but those are double type values, so comparing them by == is risky
                if(entryLowestTimestamp <= lowestValue) {
                    if (entryLowestTimestamp == lowestValue) {
                        id.add(entry.getKey());
                    } else {
                        lowestValue = entryLowestTimestamp;
                        id.clear();
                        id.add(entry.getKey());
                    }
                }
            }
        }

        if(id.size() == 1)
            return id.get(0);
        else {
            int index = (int)(id.size() * Math.random());
            return id.get(index);
        }
    }

    private Packet wfqArrivalAlgorithm(Event event) {
        int id = event.getQueueId();
        double vst = queues.get(id).getVirtualSpacingTimestamp();
        double ri = queues.get(id).getWeight();
        int packetSize = sources.get(id).getPacketSizeInBytes();

        double timestamp = Math.max(server.getSpacingTime(), vst) + (packetSize/ri);
        queues.get(id).setVirtualSpacingTimestamp(timestamp);
        return new Packet(timestamp, packetSize);
    }

    private boolean areAllQueuesEmpty() {
        boolean flag = true;
        for(QueuePQWFQ queue : queues.values()) {
            if(!queue.isEmpty())
                flag = false;
        }
        return flag;
    }

    private boolean hasHighPriorityQueue() {
        for(Map.Entry<Integer, QueuePQWFQ> q : queues.entrySet()) {
            if(q.getValue().getPriority() == QueuePQWFQ.HIGH_PRIORITY)
                return true;
        }
        return false;
    }

    private int getHighPriorityQueueId() throws NoSuchElementException {
        for(Map.Entry<Integer, QueuePQWFQ> q : queues.entrySet()) {
            if(q.getValue().getPriority() == QueuePQWFQ.HIGH_PRIORITY)
                return q.getKey();
        }
        throw new NoSuchElementException("No such queue!");
    }

    private void updateStatistics(double timeDelta) {
        if(isServerBusy())
            statistics.increaseServerBusyTime(timeDelta);

        queues.forEach((id, queue) -> statistics.increaseQueueTime(id,queue.size() * timeDelta));
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
        queues.forEach((id, queue) -> {
            java.lang.System.out.println("-----------------------------------------------");
            java.lang.System.out.println("QUEUE " + id +  " (priority: " + queue.getPriority() + ")");
            Statistics.QueueStatistics stats = statistics.getQueueStatistics(id);
            java.lang.System.out.println("Number of delays: " + stats.numberOfDelays);
            java.lang.System.out.println("Total delay: " + stats.totalDelay);
            java.lang.System.out.println("Q(t): " + stats.queueTime);
            java.lang.System.out.println("Clients in queue: " + queue.size());
            if(!queue.isEmpty())
                java.lang.System.out.println("Lowest timestamp: " + queue.peekLowestTimestamp());
        });
        java.lang.System.out.println("Next event: " + eventList.peekNextEvent().getEventType());
        java.lang.System.out.println("Source number: " + eventList.peekNextEvent().getQueueId());
        java.lang.System.out.println("-----------------------------------------------\n");
    }
}
