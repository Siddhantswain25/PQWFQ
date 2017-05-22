import java.util.*;
import java.util.function.BiConsumer;

public class Server {
    private boolean isBusy;
    private HashMap<Integer, QueuePQWFQ> queues;

    public Server() {
        queues = new HashMap<>();
    }

    public void addQueue(int queueId, int priority, double weight) {
        queues.put(queueId, new QueuePQWFQ(priority, weight));
    }

    public void addQueue(int queueId, QueuePQWFQ queue) {
        queues.put(queueId, queue);
        //TODO: Should there be only one HIGH_PRIORITY queue and many LOW_PRIORITY?
        //TODO: Should method check if sum of weights does not exceed 1?
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void addClient(int queueId, double arrivalTime) {
        queues.get(queueId).add(arrivalTime);
    }

    public double handleNextClient(int queueId) {
        return queues.get(queueId).poll();
    }

    public boolean isQueueEmpty(int queueId) {
        return queues.get(queueId).isEmpty();
    }

    public int getQueueSize(int queueId) {
        return queues.get(queueId).size();
    }

    public int getNumberOfQueues() {
        return queues.size();
    }

    public QueuePQWFQ getQueue(int queueId) {
        return queues.get(queueId);
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
}
