import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.*;
import java.lang.System;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomGeneratorTest {
    private ArrayList<Double> times;
    private double mean;

    @BeforeEach
    void setUp() {
        times = new ArrayList<>();
        mean = 0.1;
        Clock.reset();
    }

    @AfterEach
    void tearDown() {
        times = null;
    }

    @Test
    void getExpRandom() {
        for(int i = 0; i < 1000; i++) {
            double nextDepartureTime = RandomGenerator.getExpRandom(1/mean);
            times.add(nextDepartureTime);
        }

        System.out.println("------  EXP ------");
        System.out.println("---- mi = " + mean + " ----");
        RandomGenerator.printAllNumbers(times);
        System.out.println("---- mi = " + mean + " ----");
        RandomGenerator.drawHistogram(times);
    }

    @Test
    void getPoissonRandom() {
        for(int i = 0; i < 10000; i++) {
            double nextArrivalTime = RandomGenerator.getPoissonRandom(mean);
            times.add(nextArrivalTime);
        }

        System.out.println("----  POISSON ----");
        System.out.println("-- lambda = " + mean + " --");
        RandomGenerator.printAllNumbers(times);
        System.out.println("-- lambda = " + mean + " --");
        RandomGenerator.drawHistogram(times);
    }

    @Test
    void getOnOffRandom() {
        double t1 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(0.1 - t1) < 0.00000001);
        Clock.setTime(t1);
        double t2 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(0.2 - t2) < 0.00000001);
        Clock.setTime(t2);
        double t3 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(0.3 - t3) < 0.00000001);
        Clock.setTime(t3);
        double t4 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(0.4 - t4) < 0.00000001);
        Clock.setTime(t4);
        double t5 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(0.5 - t5) < 0.00000001);
        Clock.setTime(t5);
        double t6 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(1.5 - t6) < 0.00000001);
        Clock.setTime(t6);
        double t7 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(1.6 - t7) < 0.00000001);
        Clock.setTime(t7);
        double t8 = RandomGenerator.getDeterministicOnOffValue(0.5, 1.0, 10);
        assertTrue(Math.abs(1.7 - t8) < 0.00000001);
        Clock.setTime(t8);
    }


}