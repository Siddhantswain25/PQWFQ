public class OnOffDeterministicPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private int packetsPerSecond;
    private int packetSizeInBytes;

    public OnOffDeterministicPacketGenerationStrategy(double onDuration, double offDuration, int packetsPerSecond,
                                                      int packetSizeInBytes) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.packetsPerSecond = packetsPerSecond;
        this.packetSizeInBytes = packetSizeInBytes;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        return generator.getNextOnOffDeterministic(onDuration, offDuration, packetsPerSecond, packetSizeInBytes);
    }
}
