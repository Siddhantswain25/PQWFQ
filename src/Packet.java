public class Packet {
    private final double arrivalTime;
    private final double virtualSpacingTimestamp;

    public Packet(double virtualSpacingTimestamp) {
        arrivalTime = Clock.getCurrentTime();
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
    }

    public Packet(double arrivalTime, double virtualSpacingTimestamp) {
        this.arrivalTime = arrivalTime;
        this.virtualSpacingTimestamp = virtualSpacingTimestamp;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getVirtualSpacingTimestamp() {
        return virtualSpacingTimestamp;
    }
}
