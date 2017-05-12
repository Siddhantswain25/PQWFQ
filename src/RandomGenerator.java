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
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }
}
