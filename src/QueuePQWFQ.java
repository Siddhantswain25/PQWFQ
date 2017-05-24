import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;


public class QueuePQWFQ {
    public static final int HIGH_PRIORITY = 10;
    public static final int LOW_PRIORITY = 1;

    private Queue<Packet> queue;
    private int priority;
    private double weight; //ri
    private double virtualSpacingTimestamp; //VSi

    QueuePQWFQ(int priority, double weight) {
        queue = new PriorityQueue<>(5, Comparator.comparingDouble(Packet::getVirtualSpacingTimestamp));
        this.priority = priority;
        this.weight = weight;
        virtualSpacingTimestamp = 0.0; //TODO: change it
    }

    public void add(Packet packet) {
        queue.add(packet);
    }

    public Packet poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getPriority() {
        return priority;
    }

    public double getWeight() {
        return weight;
    }

    public double getVirtualSpacingTimestamp() {
        return virtualSpacingTimestamp;
    }

    public void setVirtualSpacingTimestamp(double virtualSpacingTimestamp) {
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
    }

    public double peekLowestTimestamp() {
        return queue.peek().getVirtualSpacingTimestamp();
    }

    public int size() {
        return queue.size();
    }
}
