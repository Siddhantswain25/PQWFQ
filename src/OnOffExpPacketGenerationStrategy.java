public class OnOffExpPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private int burstRateInBps;
    private int packetSizeInBytes;
    private int packetsSentInCurrentBurst;
    private double nextOnDuration;
    private double nextOffDuration;

    public OnOffExpPacketGenerationStrategy(double onDuration, double offDuration, int burstRateInBps,
                                            int packetSizeInBytes) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.burstRateInBps = burstRateInBps;
        this.packetSizeInBytes = packetSizeInBytes;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        double currentTime = Clock.getCurrentTime();

        if(!generator.isRunning() && currentTime >= generator.getStartTime()) {
            generator.setRunning(true);
            nextOnDuration = generator.getExpRandom(onDuration);
            nextOffDuration = generator.getExpRandom(offDuration);
        }
        double interval = packetSizeInBytes/(double)burstRateInBps;
        double burstLength = nextOnDuration/interval; //in packets


        //TODO: set next off duration
        if(generator.isRunning() && packetsSentInCurrentBurst < burstLength) {
            packetsSentInCurrentBurst++;
            return interval;
        } else {
            packetsSentInCurrentBurst = 0;
            nextOnDuration = generator.getExpRandom(onDuration);
            return generator.getExpRandom(offDuration);
        }
    }
}
