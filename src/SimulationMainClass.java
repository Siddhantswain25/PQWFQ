import java.lang.*;

public class SimulationMainClass {
    public static void main(String[] args) {

        System system = new System(1, 10);
        //system.displayTrace();

        int N = 70000000;
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            //system.displayTrace();
        }
        system.displayAllStatistics();
    }
}
