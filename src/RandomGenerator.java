import java.lang.*;
import java.lang.System;
import java.util.ArrayList;
import java.util.Random;
import static java.lang.StrictMath.log;
/*
Random.nextDouble();
Returns the next pseudorandom, uniformly distributed double value
between 0.0 and 1.0 from this random number generator's sequence.
 */

public abstract class RandomGenerator {

    private final static double MIN_SERVICE_TIME = 0.2;
    private final static double MIN_ARRIVAL_INTERVAL = 0.000001;

    public static double getExpRandom(double mean) { //service times

        Random r = new Random();/*
        double random;
        do {
            random = r.nextDouble();
        } while (random == 0.0);

        double result = -mean*log(random);*/
        double result = Math.log(1-r.nextDouble())/(-mean);
        if(result != 0)
            return result;
        else
            return MIN_ARRIVAL_INTERVAL;
    }

    public static double getPoissonRandom(double mean) { //arivals
        Random r = new Random();
        double limit = Math.exp(-mean);
        double prod = r.nextDouble();
        int n;
        for (n = 0; prod >= limit; n++)
            prod *= r.nextDouble();
        if (n != 0)
            return n;
        else
            return MIN_SERVICE_TIME;
    }

    public static double getOnOffRandom(double onDuration, double offDuration, double packetsPerSecond) {
        double currentTime = Clock.getCurrentTime(); //TODO: static method with get current time?
        double period = onDuration + offDuration;
        double difference = currentTime % period;

        if(difference < onDuration) {
            return currentTime + 1/packetsPerSecond;
        } else {
            return currentTime + (period - difference);
        }
    }

    public static void drawHistogram(ArrayList<Double> numbers) {
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

    private static String convertToStars(int num) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < num; j++) {
            builder.append('*');
        }
        return builder.toString();
    }

    public static void printAllNumbers(ArrayList<Double> numbers) {
        System.out.println("----------- START -----------");
        for(Double d : numbers)
            System.out.println(d);
        System.out.println("------------ END ------------");
    }
}
