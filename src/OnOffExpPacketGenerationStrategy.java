public class OnOffExpPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private int burstRateInBps;
    private int packetSizeInBytes;

    public OnOffExpPacketGenerationStrategy(double onDuration, double offDuration, int burstRateInBps,
                                            int packetSizeInBytes) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.burstRateInBps = burstRateInBps;
        this.packetSizeInBytes = packetSizeInBytes;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        return generator.getNextOnOffExp(onDuration, offDuration, burstRateInBps, packetSizeInBytes);
    }
}
