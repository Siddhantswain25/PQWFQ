import java.util.Queue;
import java.util.LinkedList;


public class QueuePQWFQ {
    public static final int HIGH_PRIORITY = 10;
    public static final int LOW_PRIORITY = 1;

    private Queue<Double> queue;
    private int priority;
    private double weight;

    QueuePQWFQ(int priority, double weight) {
        queue = new LinkedList<>();
        this.priority = priority;
        this.weight = weight;
    }

    public void add(double arrivalTime) {
        queue.add(arrivalTime);
    }

    public double poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
