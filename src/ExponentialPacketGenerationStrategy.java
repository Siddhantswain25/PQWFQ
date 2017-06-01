public class ExponentialPacketGenerationStrategy implements PacketGenerationStrategy {
    private double lambda;

    public ExponentialPacketGenerationStrategy(double lambda) {
        this.lambda = lambda;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        return generator.getNextExp(lambda);
    }
}
