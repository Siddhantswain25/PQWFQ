package components;

import source.Packet;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueuePQWFQ {
    public static final int HIGH_PRIORITY = 10;
    public static final int LOW_PRIORITY = 1;

    private Queue<Packet> queue;
    private int priority;
    private double bandwidth; //reserved bandwidth in b/s
    private double virtualSpacingTimestamp; //VSi

    QueuePQWFQ(int priority, double bandwidth) {
        queue = new PriorityQueue<>(5, Comparator.comparingDouble(Packet::getVirtualSpacingTimestamp));
        this.priority = priority;
        this.bandwidth = bandwidth;
        this.virtualSpacingTimestamp = 0.0;
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

    public double getBandwidth() {
        return bandwidth;
    }

    public double getVirtualSpacingTimestamp() {
        return virtualSpacingTimestamp;
    }

    public void setVirtualSpacingTimestamp(double virtualSpacingTimestamp) {
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
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
