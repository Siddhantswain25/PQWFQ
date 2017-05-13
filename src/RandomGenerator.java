import java.lang.*;
import java.util.ArrayList;
import java.util.Random;
import static java.lang.StrictMath.log;
/*
Random.nextDouble();
Returns the next pseudorandom, uniformly distributed double value
between 0.0 and 1.0 from this random number generator's sequence.
 */

public abstract class RandomGenerator {
    public static double getExpRandom(double mean) { //service times
        Random r = new Random();
        double random;
        do {
            random = r.nextDouble();
        } while (random == 0.0);

        return -mean*log(random);
    }

    public static double getPoissonRandom(double mean) { //arivals
        Random r = new Random();
        /*
        double limit = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > limit);
        return k - 1;
        */

        double limit = Math.exp(-mean);
        double prod = r.nextDouble();
        int n;
        for (n = 0; prod >= limit; n++)
            prod *= r.nextDouble();
        return n;
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
}
