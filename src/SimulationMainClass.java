public class SimulationMainClass {
    public static void main(String[] args) {
        System system = new System();
        while(Clock.getCurrentTime() < 1000.0) {
            system.processNextEvent();
        }
        system.displayAllStatistics();
    }
}
