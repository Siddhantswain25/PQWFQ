public class ExponentialPacketGenerationStrategy implements PacketGenerationStrategy {
    private double mean;

    public ExponentialPacketGenerationStrategy(double mean) {
        this.mean = mean;
    }

    @Override
    public double getTimeToNextArrival() {
        return RandomGenerator.getExpRandom(mean);
    }
}
