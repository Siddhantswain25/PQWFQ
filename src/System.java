import java.util.*;
import java.util.function.BiConsumer;

public class System {
    private EventList eventList;
    private Server server;
    private HashMap<Integer, QueuePQWFQ> queues;
    private HashMap<Integer, Source> sources;
    private HashMap<Integer, PacketGenerationStrategy> strategies;
    private PacketGenerationStrategy defaultStrategy;
    private Statistics statistics;

    System(Server server) {
        eventList = new EventList();
        queues = new HashMap<>();
        sources = new HashMap<>();
        strategies = new HashMap<>();
        this.server = server;
        statistics = new Statistics();
    }

    public void addQueue(int queueId, int priority, double weight) throws IllegalArgumentException {
        addQueue(queueId, new QueuePQWFQ(priority, weight));
    }

    public void addQueue(int queueId, QueuePQWFQ queue) throws IllegalArgumentException {
        if(queue.getPriority() == QueuePQWFQ.HIGH_PRIORITY && hasHighPriorityQueue())
            throw new IllegalArgumentException("Server already has a high priority queue!");
        else if(queues.containsKey(queueId))
            throw new IllegalArgumentException("Server already has queue with such id!");
        else {
            queues.put(queueId, queue);
            statistics.registerQueue(queueId);
        }
        //TODO: Should method check if sum of weights does not exceed 1?
    }

    public void addSource(int sourceId, double startTime, int packetSizeInBytes) {
        sources.put(sourceId, new Source(startTime, packetSizeInBytes));
        strategies.put(sourceId, defaultStrategy);
    } //TODO: throw exceptions

    public void addSource(int sourceId, double startTime, int packetSizeInBytes, PacketGenerationStrategy strategy) {
        sources.put(sourceId, new Source(startTime, packetSizeInBytes));
        strategies.put(sourceId, strategy);
        scheduleNextArrival(sourceId);
    }

    public void setStrategy(int sourceId, PacketGenerationStrategy strategy) {
        strategies.put(sourceId, strategy);
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
            addDelayToStatistics(queueId, 0.0);
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
            addDelayToStatistics(queueId, waitingTime);
            scheduleNextDeparture(handledPacket.getSize());
        }
    }

    private void scheduleNextArrival(int id) {
        double timeToNextArrival = strategies.get(id).getTimeToNextArrival(sources.get(id));
        statistics.increaseSumOfArrivalIntervals(timeToNextArrival);
        double nextArrivalTime = Clock.getCurrentTime() + timeToNextArrival;
        addEvent(new Event(EventType.ARRIVAL, nextArrivalTime, id));
    }

    private void scheduleNextDeparture(int packetSizeInBytes){
        double serviceTime = (packetSizeInBytes*8)/server.getServiceBitrate();
        statistics.increaseTotalServiceTime(serviceTime);
        double nextDepartureTime = Clock.getCurrentTime() + serviceTime;
        addEvent(new Event(EventType.DEPARTURE, nextDepartureTime));
    }

    private void forEachQueue(BiConsumer<Integer, QueuePQWFQ> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<Integer, QueuePQWFQ> entry : queues.entrySet()) {
            Integer k;
            QueuePQWFQ v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch(IllegalStateException ise) {
                throw new ConcurrentModificationException(ise); //entry is no longer in the map
            }
            action.accept(k, v);
        }
    }

    private Set<Integer> getSetOfQueueIds() {
        return queues.keySet();
    }

    private int pqwfqDepartureAlgorithm() {
        if(hasHighPriorityQueue()) {
            int id;
            try {
                id = getHighPriorityQueueId();
                if(!queues.get(id).isEmpty())
                    return id;
            } catch(NoSuchQueueException e) {
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
        return id.stream()
                .skip((int)(id.size() * Math.random()))
                .findFirst()
                .orElse(1); //TODO: well... find a more elegant way
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

    private Packet handleNextClient(int queueId) {
        return queues.get(queueId).poll();
    }

    private boolean areAllQueuesEmpty() {
        boolean flag = true;
        for(QueuePQWFQ queue : queues.values()) {
            if(!queue.isEmpty())
                flag = false;
        }
        return flag;
    }

    private int getQueueSize(int queueId) {
        return queues.get(queueId).size();
    }

    private boolean hasHighPriorityQueue() {
        for(Map.Entry<Integer, QueuePQWFQ> q : queues.entrySet()) {
            if(q.getValue().getPriority() == QueuePQWFQ.HIGH_PRIORITY)
                return true;
        }
        return false;
    }

    private int getHighPriorityQueueId() throws NoSuchQueueException {
        for(Map.Entry<Integer, QueuePQWFQ> q : queues.entrySet()) {
            if(q.getValue().getPriority() == QueuePQWFQ.HIGH_PRIORITY)
                return q.getKey();
        }
        throw new NoSuchQueueException();
    }

    private void updateStatistics(double timeDelta) {
        if(isServerBusy())
            statistics.increaseServerBusyTime(timeDelta);

        forEachQueue((id, queue) -> statistics.increaseQueueTime(id,queue.size() * timeDelta));
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
        forEachQueue((id, queue) -> {
            java.lang.System.out.println("-----------------------------------------------");
            java.lang.System.out.println("QUEUE " + id);
            Statistics.QueueStatistics stats = statistics.getQueueStatistics(id);
            java.lang.System.out.println("Number of delays: " + stats.numberOfDelays);
            java.lang.System.out.println("Total delay: " + stats.totalDelay);
            java.lang.System.out.println("Q(t): " + stats.queueTime);
            java.lang.System.out.print("Clients in queue " + id + ": " + getQueueSize(id));
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
