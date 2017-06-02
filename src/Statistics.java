import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public class Statistics {
    public class QueueStatistics {
        public int numberOfDelays = 0;
        public double totalDelay = 0.0;
        public double queueTime = 0.0;
    } //TODO: fix encapsulation?

    private double totalServiceTime;
    private long numberOfArrivals;
    private double sumOfArrivalIntervals;
    private double serverBusyTime;
    private HashMap<Integer, QueueStatistics> queueStatistics;

    public Statistics() {
        queueStatistics = new HashMap<>();
        resetAllStatistics();
    }

    public void registerQueue(int queueId) {
        queueStatistics.put(queueId, new QueueStatistics());
    }

    public void resetAllStatistics() {
        totalServiceTime = 0.0;
        numberOfArrivals = 0;
        sumOfArrivalIntervals = 0.0;
        serverBusyTime = 0.0;
        queueStatistics.replaceAll((id, qs) -> qs = new QueueStatistics());
    }

    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    public void increaseTotalServiceTime(double totalServiceTime) {
        this.totalServiceTime += totalServiceTime;
    }

    public long getNumberOfArrivals() {
        return numberOfArrivals;
    }

    public void increaseNumberOfArrivals() {
        this.numberOfArrivals++;
    }

    public double getSumOfArrivalIntervals() {
        return sumOfArrivalIntervals;
    }

    public void increaseSumOfArrivalIntervals(double sumOfArrivalIntervals) {
        this.sumOfArrivalIntervals += sumOfArrivalIntervals;
    }

    public int getNumberOfDelays(int queueId) {
        return queueStatistics.get(queueId).numberOfDelays;
    }

    public void increaseNumberOfDelays(int queueId) {
        this.queueStatistics.get(queueId).numberOfDelays++;
    }

    public double getTotalDelay(int queueId) {
        return queueStatistics.get(queueId).totalDelay;
    }

    public void increaseTotalDelay(int queueId, double totalDelay) {
        this.queueStatistics.get(queueId).totalDelay += totalDelay;
    }

    public double getQueueTime(int queueId) {
        return queueStatistics.get(queueId).queueTime;
    }

    public void increaseQueueTime(int queueId, double queueTime) {
        this.queueStatistics.get(queueId).queueTime += queueTime;
    }

    public double getServerBusyTime() {
        return serverBusyTime;
    }

    public void increaseServerBusyTime(double serverBusyTime) {
        this.serverBusyTime += serverBusyTime;
    }

    private void forEachQueueStatistic(BiConsumer<Integer, QueueStatistics> action) {
        queueStatistics.forEach(action);
    }

    public QueueStatistics getQueueStatistics(int queueId) {
        return queueStatistics.get(queueId);
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        HashMap<Integer, Double> dn = new HashMap<>();
        HashMap<Integer, Double> qn = new HashMap<>();

        forEachQueueStatistic((id, stat) -> {
            dn.put(id, stat.totalDelay/stat.numberOfDelays);
            qn.put(id, stat.queueTime/totalSimTime);
        });

        double un = serverBusyTime/totalSimTime; //TODO: total load, and for each queue
        double avgArrivalInterval = sumOfArrivalIntervals/numberOfArrivals;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Total simulation time: " + String.format("%.5f", totalSimTime));
        java.lang.System.out.println("Number of arrivals: " + numberOfArrivals);
        java.lang.System.out.println("-----------------------------------------------");
        forEachQueueStatistic((id, qs) -> {
            java.lang.System.out.println("QUEUE " + id);
            java.lang.System.out.println("Number of delays/serviced customers: " + qs.numberOfDelays);
            java.lang.System.out.println("Total delay: " + String.format("%.3f", qs.totalDelay));
            java.lang.System.out.println("Q(t): " + String.format("%.3f", qs.queueTime));
            java.lang.System.out.println("Average waiting time:\t" + String.format("%.3f", dn.get(id)));
            java.lang.System.out.println("Average queue size:\t" + String.format("%.3f", qn.get(id)));
            java.lang.System.out.println("Rho (load): ");
            java.lang.System.out.println("-----------------------------------------------");
        });
        java.lang.System.out.println("B(t): " + String.format("%.3f", serverBusyTime));
        java.lang.System.out.println("Average load: " + String.format("%.3f", un));
        java.lang.System.out.println("Average arrival interval: " + String.format("%.3f", avgArrivalInterval));
        java.lang.System.out.println("-----------------------------------------------");
    }
}
