public class OnOffDeterministicPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private int packetSizeInBytes;
    private int burstRateInBps;
    private int packetsSentInCurrentBurst;

    public OnOffDeterministicPacketGenerationStrategy(double onDuration, double offDuration, int packetSizeInBytes, int burstRateInBps) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.packetSizeInBytes = packetSizeInBytes;
        this.burstRateInBps = burstRateInBps;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        double currentTime = Clock.getCurrentTime();
        double period = onDuration + offDuration;
        double difference = currentTime % period;
        double interval = (packetSizeInBytes*8)/burstRateInBps;
        double burstLength = onDuration/interval; //in packets

        if(!generator.isRunning() && currentTime >= generator.getStartTime())
            generator.setRunning(true);

        if(generator.isRunning() && packetsSentInCurrentBurst < burstLength) {
            packetsSentInCurrentBurst++;
            return interval;
        } else {
            packetsSentInCurrentBurst = 0;
            return period - difference; //next event will appear at the beginning of next ON period
        }
    }
}
