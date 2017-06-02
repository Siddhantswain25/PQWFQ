import java.lang.*;

public class OnOffExpPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private double burstRateInBps;
    private double intervalBetweenPackets;
    private double startOfNextBurst;
    private double startOfNextIdle;
    private double totalIdleDuration; //TODO: clear it from debugging methods
    private double totalBurstDuration;
    private int totalPeriods;

    public OnOffExpPacketGenerationStrategy(double onDuration, double offDuration, int burstRateInBps) {
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.burstRateInBps = burstRateInBps;
    }

    @Override
    public double getTimeToNextArrival(Source generator) {
        double currentTime = Clock.getCurrentTime();

        if(!generator.isRunning() && currentTime >= generator.getStartTime()) {
            generator.setRunning(true);
            startOfNextBurst = currentTime;
            startOfNextIdle = startOfNextBurst + generator.getExpRandom(offDuration);
            intervalBetweenPackets =  generator.getPacketSizeInBytes()/burstRateInBps;
        }

        double difference = Math.abs(currentTime - startOfNextIdle);

        if(generator.isRunning() && currentTime < startOfNextIdle && difference > 1e-10) {
            return intervalBetweenPackets;
        } else {
            double idleDuration = generator.getExpRandom(offDuration);
            totalIdleDuration += idleDuration;
            startOfNextBurst = currentTime + idleDuration;
            double nextBurstDuration = generator.getExpRandom(onDuration);
            totalBurstDuration += nextBurstDuration;
            startOfNextIdle = startOfNextBurst + nextBurstDuration;
            totalPeriods++;
            //java.lang.System.out.println("Avg burst duration: " + (totalBurstDuration/(double)totalPeriods));
            //java.lang.System.out.println("Avg idle duration: " + (totalIdleDuration/(double)totalPeriods));
            return idleDuration;
        }
    }
}
