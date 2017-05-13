import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.*;
import java.lang.System;
import java.util.ArrayList;

class RandomGeneratorTest {
    private ArrayList<Double> times;
    private double lambda;
    private double mi;

    @BeforeEach
    void setUp() {
        times = new ArrayList<>();
        lambda = 3;
        mi = 6;
    }

    @AfterEach
    void tearDown() {
        times = null;
    }

    @Test
    void getExpRandom() {
        for(int i = 0; i < 1000; i++) {
            double nextDepartureTime = RandomGenerator.getExpRandom(1/mi);
            times.add(nextDepartureTime);
        }

        System.out.println("------  EXP ------");
        System.out.println("---- mi = " + mi + " ----");
        RandomGenerator.printAllNumbers(times);
        System.out.println("---- mi = " + mi + " ----");
        RandomGenerator.drawHistogram(times);
    }

    @Test
    void getPoissonRandom() {
        for(int i = 0; i < 10000; i++) {
            double nextArrivalTime = RandomGenerator.getPoissonRandom(lambda);
            times.add(nextArrivalTime);
        }

        System.out.println("----  POISSON ----");
        System.out.println("-- lambda = " + lambda + " --");
        RandomGenerator.printAllNumbers(times);
        System.out.println("-- lambda = " + lambda + " --");
        RandomGenerator.drawHistogram(times);
    }


}