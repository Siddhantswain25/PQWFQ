public class Clock {
    private static Clock instance = null;
    private Clock() {
        currentTime = 0.0;
    }

    private double currentTime;

    public static Clock getInstance() {
        if(instance == null)
            instance = new Clock();
        return instance;
    }

    public static void reset() {
        getInstance().currentTime = 0.0;
    }

    public static double getCurrentTime() {
        return getInstance().currentTime;
    }

    public static void incrementTime(double time) {
        getInstance().currentTime += time;
    }
}
