import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.*;
import java.lang.System;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SourceTest {
    private Source generator;
    private ArrayList<Double> times;
    private double mean;

    @BeforeEach
    void setUp() {
        times = new ArrayList<>();
        mean = 0.1;
        generator = new Source(0, 70);
        Clock.reset();
    }

    @AfterEach
    void tearDown() {
        times = null;
    }

    @Test
    void getExpRandom() {
        double actualMean = 0;
        for(int i = 0; i < 1000; i++) {
            double value = generator.getNextExp(1/mean);
            times.add(value);
            actualMean += value;
        }

        actualMean = actualMean/times.size();

        System.out.println("------  EXP ------");
        System.out.println("---- mi = " + mean + " ----");
        generator.printAllNumbers(times);
        System.out.println("mi = " + mean);
        System.out.println("actual mean = " + actualMean);
        generator.drawHistogram(times);
    }

    @Test
    void getPoissonRandom() {
        for(int i = 0; i < 10000; i++) {
            double nextArrivalTime = generator.getNextPoisson(mean);
            times.add(nextArrivalTime);
        }

        System.out.println("----  POISSON ----");
        System.out.println("-- lambda = " + mean + " --");
        generator.printAllNumbers(times);
        System.out.println("-- lambda = " + mean + " --");
        generator.drawHistogram(times);
    }
/*
    @Test
    void getOnOffRandom() {
        double t1 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(0.1 - t1) < 0.00000001);
        Clock.setTime(t1);
        double t2 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(0.2 - t2) < 0.00000001);
        Clock.setTime(t2);
        double t3 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(0.3 - t3) < 0.00000001);
        Clock.setTime(t3);
        double t4 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(0.4 - t4) < 0.00000001);
        Clock.setTime(t4);
        double t5 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(0.5 - t5) < 0.00000001);
        Clock.setTime(t5);
        double t6 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(1.5 - t6) < 0.00000001);
        Clock.setTime(t6);
        double t7 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(1.6 - t7) < 0.00000001);
        Clock.setTime(t7);
        double t8 = generator.getNextOnOffDeterministic(0.5, 1.0, 10);
        assertTrue(Math.abs(1.7 - t8) < 0.00000001);
        Clock.setTime(t8);
    }
*/

}