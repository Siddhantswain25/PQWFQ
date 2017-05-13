import java.lang.*;

public class SimulationMainClass {
    public static void main(String[] args) {
        System system = new System(2, 4);
        //system.displayTrace();
        double timeLimit = 1E8;
        while(Clock.getCurrentTime() < timeLimit) {
            system.processNextEvent();
            //system.displayTrace();
        }
        system.displayAllStatistics();
    }

    private static void progressBar(double progressPercentage) {
    }
}
