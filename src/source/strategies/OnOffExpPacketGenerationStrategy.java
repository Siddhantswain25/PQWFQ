package source.strategies;

import components.Clock;
import source.Source;

import java.lang.*;

public class OnOffExpPacketGenerationStrategy implements PacketGenerationStrategy {
    private double onDuration;
    private double offDuration;
    private double burstRateInBps;
    private double intervalBetweenPackets;
    private double startOfNextBurst;
    private double startOfNextIdle;

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
            startOfNextBurst = currentTime + idleDuration;
            double nextBurstDuration = generator.getExpRandom(onDuration);
            startOfNextIdle = startOfNextBurst + nextBurstDuration;
            return idleDuration;
        }
    }
}
