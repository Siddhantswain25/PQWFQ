import java.lang.*;
import java.lang.System;
import java.util.ArrayList;
import java.util.Random;
/*
Random.nextDouble();
Returns the next pseudorandom, uniformly distributed double value
between 0.0 and 1.0 from this random number generator's sequence.
*/

//TODO: test class
public class Source {
    private final static double MIN_ARRIVAL_INTERVAL = 0.000001;

    private PacketGenerationStrategy strategy;
    private Random random;

    private double startTime;
    private boolean started;
    private int packetsSentInCurrentBurst;
    private double nextOnDuration;
    private double nextOffDuration;

    public Source(double startTime, PacketGenerationStrategy strategy) {
        random = new Random();
        this.startTime = startTime;
        this.strategy = strategy;
        started = false;
        packetsSentInCurrentBurst = 0;
        nextOnDuration = 0;
        nextOffDuration = 0;
    }

    public void setStrategy(PacketGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    public PacketGenerationStrategy getStrategy() {
        return strategy;
    }

    public double getNextExp(double lambda) {
        if(started || startTime == 0) {
            double result = getExpRandom(1/lambda);
            if (result != 0)
                return result;
            else
                return MIN_ARRIVAL_INTERVAL;
        } else {
            started = true;
            return startTime;
        }
    }

    public double getNextPoisson(double mean) {
        if(started || startTime == 0) {
            double n = getPoissonRandom(mean);
            return n != 0 ? n : MIN_ARRIVAL_INTERVAL;
        } else {
            started = true;
            return startTime;
        }
    }

    //TODO: test
    public double getNextOnOffExp(double onDuration, double offDuration, int burstRateInBps, int packetSizeInBytes) {
        double currentTime = Clock.getCurrentTime();

        if(!started && currentTime >= startTime) {
            started = true;
            nextOnDuration = getExpRandom(nextOnDuration);
            nextOffDuration = getNextExp(nextOffDuration);
        }
        double interval = (packetSizeInBytes*8)/burstRateInBps;
        double burstLength = nextOnDuration/interval; //in packets

        if(started && packetsSentInCurrentBurst < burstLength) {
            packetsSentInCurrentBurst++;
            return getExpRandom(interval);
        } else {
            packetsSentInCurrentBurst = 0;
            nextOnDuration = getExpRandom(onDuration);
            return getExpRandom(offDuration);
        }

    }

    //TODO: test
    public double getNextOnOffDeterministic(double onDuration, double offDuration, double burstRateInBps,
                                            int packetSizeInBytes) {
        double currentTime = Clock.getCurrentTime();
        double period = onDuration + offDuration;
        double difference = currentTime % period;
        double interval = (packetSizeInBytes*8)/burstRateInBps;
        double burstLength = onDuration/interval; //in packets

        if(!started && currentTime >= startTime)
            started = true;

        if(started && packetsSentInCurrentBurst < burstLength) {
            packetsSentInCurrentBurst++;
            return interval;
        } else {
            packetsSentInCurrentBurst = 0;
            return period - difference; //next event will appear at the beginning of next ON period
        }
    }

    private double getExpRandom(double mean) {
        return Math.log(1 - random.nextDouble()) / (-mean);
    }

    private double getPoissonRandom(double mean) {
        double limit = Math.exp(-mean);
        double prod = random.nextDouble();
        int n;
        for (n = 0; prod >= limit; n++)
            prod *= random.nextDouble();
        return n;
    }

    //for debugging purposes
    public void drawHistogram(ArrayList<Double> numbers) {
        long[] array = new long[11]; //0.0-0.4(9) 0.5-0.(9) ...
        int size = numbers.size();
        int max_height = 70;
        for(Double d : numbers) {
            int rounded = (int)(d+0.5);
            if(rounded > 10)
                array[10] += max_height;
            else
                array[rounded] += max_height;
        }
        System.out.println("----------- HISTOGRAM -----------");
        for(long d : array) {
            java.lang.System.out.println(convertToStars((int)(d/size)));
        }
    }

    private String convertToStars(int num) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < num; j++) {
            builder.append('*');
        }
        return builder.toString();
    }

    public void printAllNumbers(ArrayList<Double> numbers) {
        System.out.println("----------- START -----------");
        for(Double d : numbers)
            System.out.println(d);
        System.out.println("------------ END ------------");
    }


}
