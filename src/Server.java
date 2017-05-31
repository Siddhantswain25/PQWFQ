import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private double spacingTime;
    private double serviceBitrate; //C[b/s]
    private boolean isBusy;
    private HashMap<Integer, QueuePQWFQ> queues;

    public Server() {
        queues = new HashMap<>();
        this.spacingTime = 0.0;
        this.serviceBitrate = 1000.0; //1kbps
    }

    public Server(double serviceBitrate) {
        queues = new HashMap<>();
        this.spacingTime = 0.0;
        this.serviceBitrate = serviceBitrate;
    }

    public void addQueue(int queueId, int priority, double weight, int nominalPacketSizeInBytes)
            throws IllegalArgumentException {
        addQueue(queueId, new QueuePQWFQ(priority, weight, nominalPacketSizeInBytes));
    }

    public void addQueue(int queueId, QueuePQWFQ queue) throws IllegalArgumentException {
        if(queue.getPriority() == QueuePQWFQ.HIGH_PRIORITY && hasHighPriorityQueue())
            throw new IllegalArgumentException("Server already has a high priority queue!");
        else if(queues.containsKey(queueId))
            throw new IllegalArgumentException("Server already has queue with such id!");
        else
            queues.put(queueId, queue);
        //TODO: Should method check if sum of weights does not exceed 1?
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

    //TODO: 1.refactor 2.choose random queue if timestamps are the same
    private int getIdOfQueueWithTheLowestTimestampOfNextPacket() {
        double lowestValue = Double.POSITIVE_INFINITY;
        int id = 0;

        for(Map.Entry<Integer, QueuePQWFQ> entry : queues.entrySet()) {
            if(!entry.getValue().isEmpty()
                    && entry.getValue().getPriority() == QueuePQWFQ.LOW_PRIORITY
                    && entry.getValue().peekLowestTimestamp() < lowestValue) {
                lowestValue = entry.getValue().peekLowestTimestamp();
                id = entry.getKey();
            }
        }
        return id;
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
