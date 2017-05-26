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
    private int nominalPacketSize; //[B] each queue can have different packet sizes

    QueuePQWFQ(int priority, double weight, int nominalPacketSizeInBytes) {
        queue = new PriorityQueue<>(5, Comparator.comparingDouble(Packet::getVirtualSpacingTimestamp));
        this.priority = priority;
        this.weight = weight;
        this.virtualSpacingTimestamp = 0.0;
        this.nominalPacketSize = nominalPacketSizeInBytes;
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

    public int getNominalPacketSize() {
        return nominalPacketSize;
    }

    public void setNominalPacketSize(int nominalPacketSize) {
        this.nominalPacketSize = nominalPacketSize;
    }

    public double peekLowestTimestamp() {
        if(queue.isEmpty())
            return 0.0;
        else
            return queue.peek().getVirtualSpacingTimestamp();
    }

    public int size() {
        return queue.size();
    }
}
