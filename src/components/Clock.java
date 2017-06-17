package components;

public class Clock {
    private static Clock instance = null;
    private Clock() {
        currentTime = 0.0;
    }

    private double currentTime;

    private static Clock getInstance() {
        if(instance == null)
            instance = new Clock();
        return instance;
    }

    public static void reset() {
        getInstance().currentTime = 0.0;
    }

    //1 = 1 second, 0.001 = 1 millisecond
    public static double getCurrentTime() {
        return getInstance().currentTime;
    }

    public static void setTime(double time) {
        getInstance().currentTime = time;
    }

    public static void increaseTime(double timeToAdd) {
        getInstance().currentTime += timeToAdd;
    }
}
