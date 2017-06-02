public class PoissonPacketGenerationStrategy implements PacketGenerationStrategy {
    private double mean;

    public PoissonPacketGenerationStrategy(double mean) {
        this.mean = mean;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        if(generator.isRunning()) {
            double n = generator.getPoissonRandom(mean);
            return n != 0 ? n : Source.MIN_ARRIVAL_INTERVAL;
        } else {
            generator.setRunning(true);
            if(generator.getStartTime() == 0)
                return getTimeToNextArrival(generator);
            return generator.getStartTime();
        }
    }
}
