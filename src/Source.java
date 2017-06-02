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
    public final static double MIN_ARRIVAL_INTERVAL = 0.000001;

    private Random random;
    private PacketGenerationStrategy strategy;

    private double startTime;
    private int packetSizeInBytes;
    private boolean isRunning;

    public Source(double startTime, int packetSizeInBytes, PacketGenerationStrategy strategy) {
        random = new Random();
        this.strategy = strategy;
        this.startTime = startTime;
        this.packetSizeInBytes = packetSizeInBytes;
        isRunning = false;
    }

    public void setStrategy(PacketGenerationStrategy strategy) {
        this.strategy = strategy;
    }

    public int getPacketSizeInBytes() {
        return packetSizeInBytes;
    }

    public void setPacketSizeInBytes(int packetSizeInBytes) {
        this.packetSizeInBytes = packetSizeInBytes;
    }

    public double getTimeToNextArrival() {
        return strategy.getTimeToNextArrival(this);
    }

    public double getExpRandom(double mean) {
        return Math.log(1 - random.nextDouble())*(-mean);
    }

    public double getPoissonRandom(double mean) {
        double limit = Math.exp(-mean);
        double prod = random.nextDouble();
        int n;
        for (n = 0; prod >= limit; n++)
            prod *= random.nextDouble();
        return n;
    }

    public double getStartTime() {
        return startTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
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
