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

    public Statistics(Set<Integer> queueIds) {
        queueStatistics = new HashMap<>();
        queueIds.forEach(id -> queueStatistics.put(id, new QueueStatistics()));
        resetAllStatistics();
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

    public void forEachQueueStatistic(BiConsumer<Integer, QueueStatistics> action) {
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
        java.lang.System.out.println("Total simulation time: " + totalSimTime);
        java.lang.System.out.println("Number of arrivals: " + numberOfArrivals);
        java.lang.System.out.println("-----------------------------------------------");
        forEachQueueStatistic((id, qs) -> {
            java.lang.System.out.println("QUEUE " + id);
            java.lang.System.out.println("Number of delays/serviced customers: " + qs.numberOfDelays);
            java.lang.System.out.println("Total delay: " + qs.totalDelay);
            java.lang.System.out.println("Q(t): " + qs.queueTime);
            java.lang.System.out.println("Average waiting time:\t" + dn.get(id));
            java.lang.System.out.println("Average queue size:\t" + qn.get(id));
            java.lang.System.out.println("-----------------------------------------------");
        });
        java.lang.System.out.println("B(t): " + serverBusyTime);
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Average load:\t" + un);
        java.lang.System.out.println("avgArrivalInterval:\t" + avgArrivalInterval);
        java.lang.System.out.println("-----------------------------------------------");
    }
}
