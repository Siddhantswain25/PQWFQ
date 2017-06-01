import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private double spacingTime;
    private double serviceBitrate; //C[b/s]
    private boolean isBusy;
    private HashMap<Integer, QueuePQWFQ> queues;
    private HashMap<Integer, Source> sources;
    private PacketGenerationStrategy defaultStrategy;

    public Server(double serviceBitrate, double intensityForDefaultExponentialStrategy) {
        this(serviceBitrate, new ExponentialPacketGenerationStrategy(intensityForDefaultExponentialStrategy));
    }

    public Server(double serviceBitrate, PacketGenerationStrategy defaultStrategy) {
        queues = new HashMap<>();
        sources = new HashMap<>();
        this.defaultStrategy = defaultStrategy;
        this.spacingTime = 0.0;
        this.serviceBitrate = serviceBitrate;
    }

    public void addQueue(int queueId, int priority, double weight, int nominalPacketSizeInBytes)
            throws IllegalArgumentException {
        addQueue(queueId, priority, weight, nominalPacketSizeInBytes, defaultStrategy);
    }

    public void addQueue(int queueId, int priority, double weight, int nominalPacketSizeInBytes,
                         double generationIntensity) throws IllegalArgumentException {
        PacketGenerationStrategy strategy = new ExponentialPacketGenerationStrategy(generationIntensity);
        addQueue(queueId, priority, weight, nominalPacketSizeInBytes, strategy);
    }

    public void addQueue(int queueId, int priority, double weight, int nominalPacketSizeInBytes,
                         PacketGenerationStrategy strategy) throws IllegalArgumentException {
        addQueue(queueId, new QueuePQWFQ(priority, weight, nominalPacketSizeInBytes), strategy);
    }

    public void addQueue(int queueId, QueuePQWFQ queue) throws IllegalArgumentException {
        addQueue(queueId, queue, defaultStrategy);
    }

    public void addQueue(int queueId, QueuePQWFQ queue, PacketGenerationStrategy strategy)
            throws IllegalArgumentException {
        if(queue.getPriority() == QueuePQWFQ.HIGH_PRIORITY && hasHighPriorityQueue())
            throw new IllegalArgumentException("Server already has a high priority queue!");
        else if(queues.containsKey(queueId))
            throw new IllegalArgumentException("Server already has queue with such id!");
        else {
            queues.put(queueId, queue);
            sources.put(queueId, new Source(0, strategy)); //TODO: add start time?
        }
        //TODO: Should method check if sum of weights does not exceed 1?
    }

    public void setStrategy(int sourceId, PacketGenerationStrategy strategy) {
        sources.get(sourceId).setStrategy(strategy);
    }

    public double getNextArrivalTime(int sourceId) {
        Source s = sources.get(sourceId);
        return s.getStrategy().getTimeToNextArrival(s); //TODO: FIX IT!
    }

    public void addClient(int queueId, Packet packet) {
        queues.get(queueId).add(packet);
    }

    public double getServiceBitrate() {
        return serviceBitrate;
    }

    public double getSpacingTime() {
        return spacingTime;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public boolean areAllQueuesEmpty() {
        boolean flag = true;
        for(QueuePQWFQ queue : queues.values()) {
            if(!queue.isEmpty())
                flag = false;
        }
        return flag;
    }

    public int getQueueSize(int queueId) {
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

    public void setSpacingTime(double spacingTime) {
        this.spacingTime = spacingTime;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void forEachQueue(BiConsumer<Integer, QueuePQWFQ> action) {
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

    public Set<Integer> getSetOfQueueIds() {
        return queues.keySet();
    }

    public int pqwfqDepartureAlgorithm() { //TODO: refactor?
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

    public Packet wfqArrivalAlgorithm(Event event) {
        int id = event.getQueueId();
        double vst = queues.get(id).getVirtualSpacingTimestamp();
        double ri = queues.get(id).getWeight();
        int packetSize = queues.get(id).getNominalPacketSize();

        double timestamp = Math.max(spacingTime, vst) + (packetSize/ri);
        queues.get(id).setVirtualSpacingTimestamp(timestamp);
        return new Packet(timestamp, packetSize);
    }

    public Packet handleNextClient(int queueId) {
        return queues.get(queueId).poll();
    }
}
