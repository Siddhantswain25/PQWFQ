package source;

import components.Clock;

public class Packet {
    private final double arrivalTime;
    private final double virtualSpacingTimestamp;
    private final int size;

    public Packet(double virtualSpacingTimestamp, int sizeInBytes) {
        this(Clock.getCurrentTime(), virtualSpacingTimestamp, sizeInBytes);
    }

    public Packet(double arrivalTime, double virtualSpacingTimestamp, int sizeInBytes) {
        this.arrivalTime = arrivalTime;
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
        this.size = sizeInBytes;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getVirtualSpacingTimestamp() {
        return virtualSpacingTimestamp;
    }

    public int getSize() {
        return size;
    }
}
