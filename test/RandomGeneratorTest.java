import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.*;
import java.lang.System;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {
    ArrayList<Double> times;
    double lambda;
    double mi;

    @BeforeEach
    void setUp() {
        times = new ArrayList<>();
        lambda = 4;
        mi = 0.5;
    }

    @AfterEach
    void tearDown() {
        times = null;
    }

    @Test
    void getExpRandom() {
        for(int i = 0; i < 10000; i++) {
            double nextDepartureTime = RandomGenerator.getExpRandom(1/mi);
            times.add(nextDepartureTime);
        }

        System.out.println("------  EXP ------");
        RandomGenerator.drawHistogram(times);
    }

    @Test
    void getPoissonRandom() {
        for(int i = 0; i < 10000; i++) {
            double nextArrivalTime = RandomGenerator.getPoissonRandom(lambda);
            times.add(nextArrivalTime);
        }

        System.out.println("----  POISSON ----");
        RandomGenerator.drawHistogram(times);
    }


}