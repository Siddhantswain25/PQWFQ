public class OnOffDeterministicPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private int packetsPerSecond;

    public OnOffDeterministicPacketGenerationStrategy(double onDuration, double offDuration, int packetsPerSecond) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.packetsPerSecond = packetsPerSecond;
    }

    @Override
    public double getTimeToNextArrival() {
        return RandomGenerator.getDeterministicOnOffValue(onDuration, offDuration, packetsPerSecond);
    }
}
