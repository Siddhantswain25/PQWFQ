import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private double spacingTime;
    private double packetSize;

    private boolean isBusy;
    private HashMap<Integer, QueuePQWFQ> queues;

    public Server() {
        queues = new HashMap<>();
        this.packetSize = 5; //TODO: change it
        this.spacingTime = 0.0;
    }

    public void addQueue(int queueId, int priority, double weight) {
        queues.put(queueId, new QueuePQWFQ(priority, weight));
    }

    public void addQueue(int queueId, QueuePQWFQ queue) {
        queues.put(queueId, queue);
        //TODO: Should there be only one HIGH_PRIORITY queue and many LOW_PRIORITY?
        //TODO: Should method check if sum of weights does not exceed 1?
    }

    public double getSpacingTime() {
        return spacingTime;
    }

    public void setSpacingTime(double spacingTime) {
        this.spacingTime = spacingTime;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void addClient(int queueId, Packet packet) {
        queues.get(queueId).add(packet);
    }

    public boolean isQueueEmpty(int queueId) {
        return queues.get(queueId).isEmpty();
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

    private int getIdOfQueueWithTheLowestTimestampOfNextPacket() { //TODO: Refactor at least name
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

    public Packet wfqArrivalAlgorithm(Event event) {
        int id = event.getQueueId();
        double vst = queues.get(id).getVirtualSpacingTimestamp();
        double ri = queues.get(id).getWeight();

        double timestamp = Math.max(spacingTime, vst) + (packetSize/ri);
        queues.get(id).setVirtualSpacingTimestamp(timestamp);
        return new Packet(timestamp);
    }

    public Packet handleNextClient(int queueId) {
        return queues.get(queueId).poll();
    }
}
