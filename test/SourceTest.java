import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.*;
import components.Clock;

import java.lang.*;
import java.lang.System;
import java.util.ArrayList;

class SourceTest {
    private Source generator;
    private ArrayList<Double> times;
    private int N;

    @BeforeEach
    void setUp() {
        times = new ArrayList<>();
        N = 100000;
        generator = new Source(0, 100, null);
        Clock.reset();
    }

    @AfterEach
    void tearDown() {
        times = null;
    }

    @Test
    void getExpRandom() {
        double expectedMean = 0.5;
        double actualMean = 0;
        for(int i = 0; i < N; i++) {
            double value = generator.getExpRandom(expectedMean);
            actualMean += value;
        }
        actualMean = actualMean/N;
        System.out.println("expectedMean = " + expectedMean);
        System.out.println("actual mean = " + actualMean);
    }

    @Test
    void getPoissonRandom() {
        double expectedMean = 0.5;
        double actualMean = 0;
        for(int i = 0; i < N; i++) {
            double value = generator.getPoissonRandom(expectedMean);
            actualMean += value;
        }
        actualMean = actualMean/N;
        System.out.println("expectedMean = " + expectedMean);
        System.out.println("actual mean = " + actualMean);
    }

    @Test
    void getExponentialArrival() {
        double expectedMean = 0.1;
        generator.setStrategy(new ExponentialPacketGenerationStrategy(1/expectedMean));
        double actualMean = 0;
        for(int i = 0; i < N; i++) {
            double value = generator.getTimeToNextArrival();
            times.add(value);
            actualMean += value;
        }
        actualMean = actualMean/N;
        System.out.println("------  EXP ------");
        System.out.println("expectedMean = " + expectedMean);
        System.out.println("actual mean = " + actualMean);
        generator.drawHistogram(times);
    }

    @Test
    void getPoissonArrival() {
        double expectedMean = 0.1;
        double actualMean = 0;
        generator.setStrategy(new PoissonPacketGenerationStrategy(expectedMean));
        for(int i = 0; i < N; i++) {
            double value = generator.getTimeToNextArrival();
            times.add(value);
            actualMean += value;
        }

        actualMean = actualMean/times.size();

        System.out.println("----  POISSON ----");
        System.out.println("-- expectedMean = " + expectedMean + " --");
        System.out.println("-- actualMean = " + actualMean + " --");
        generator.drawHistogram(times);
    }

    @Test
    void getOnOffExpArrival() {
        PacketGenerationStrategy strategy = new OnOffExpPacketGenerationStrategy(0.5, 0.5, 1000);
        generator.setStrategy(strategy);
        for(int i = 0; i < N; i++) {
            double value = generator.getTimeToNextArrival();
            times.add(value);
            Clock.increaseTime(value);
        }
        System.out.println("----  ON/OFF EXP ----");
        for(double t : times)
            System.out.println(t);
        System.out.println("----  ON/OFF EXP ----");
    }

    @Test
    void getOnOffDeterministicArrival() {
        generator.setStrategy(new OnOffDeterministicPacketGenerationStrategy(0.5, 0.5, 1000));
        for(int i = 0; i < N; i++) {
            double value = generator.getTimeToNextArrival();
            times.add(value);
            Clock.increaseTime(value);
        }
        System.out.println("----  ON/OFF DETERMINISTIC ----");
        for(double t : times)
            System.out.println(t);
        System.out.println("----  ON/OFF DETERMINISTIC ----");
    }
}