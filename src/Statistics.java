import java.util.HashMap;

public class Statistics {
    public class QueueStatistics {
        public int numberOfDelays = 0;
        public double totalDelay = 0.0;
        public double queueTime = 0.0;
        public double arrivalInterval = 0.0;
        public long numberOfArrivals = 0;
    } //TODO: fix encapsulation?

    private long numberOfArrivals = 0;
    private double serverBusyTime = 0.0;
    private HashMap<Integer, QueueStatistics> queueStatistics = new HashMap<>();

    public void registerQueue(int queueId) {
        queueStatistics.put(queueId, new QueueStatistics());
    }

    public void addDelayToStatistics(int queueId, double delay) {
        increaseTotalDelay(queueId, delay);
        queueStatistics.get(queueId).numberOfDelays++;
    }

    public long getNumberOfArrivals() {
        return numberOfArrivals;
    }

    public void increaseNumberOfArrivals() {
        this.numberOfArrivals++;
    }

    public void increaseTotalDelay(int queueId, double totalDelay) {
        this.queueStatistics.get(queueId).totalDelay += totalDelay;
    }

    public void increaseQueueTime(int queueId, double queueTime) {
        this.queueStatistics.get(queueId).queueTime += queueTime;
    }

    public void increaseQueueArrivalInterval(int queueId, double interval) {
        this.queueStatistics.get(queueId).arrivalInterval += interval;
        this.queueStatistics.get(queueId).numberOfArrivals++;
    }

    public double getServerBusyTime() {
        return serverBusyTime;
    }

    public void increaseServerBusyTime(double serverBusyTime) {
        this.serverBusyTime += serverBusyTime;
    }

    public QueueStatistics getQueueStatistics(int queueId) {
        return queueStatistics.get(queueId);
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        HashMap<Integer, Double> dn = new HashMap<>();
        HashMap<Integer, Double> qn = new HashMap<>();
        HashMap<Integer, Double> avgArrivalInterval = new HashMap<>();

        queueStatistics.forEach((id, stat) -> {
            dn.put(id, stat.totalDelay/stat.numberOfDelays);
            qn.put(id, stat.queueTime/totalSimTime);
            avgArrivalInterval.put(id, stat.arrivalInterval/(double)stat.numberOfArrivals);
        });

        double un = serverBusyTime/totalSimTime;

        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("------------  SIMULATION RESULTS  -------------");
        java.lang.System.out.println("-----------------------------------------------");
        java.lang.System.out.println("Total simulation time: " + String.format("%.5f", totalSimTime));
        java.lang.System.out.println("Number of arrivals: " + numberOfArrivals);
        java.lang.System.out.println("-----------------------------------------------");
        queueStatistics.forEach((id, qs) -> {
            java.lang.System.out.println("QUEUE " + id);
            java.lang.System.out.println("Number of delays/serviced customers: " + qs.numberOfDelays);
            java.lang.System.out.println("Total delay: " + String.format("%.3f", qs.totalDelay));
            java.lang.System.out.println("Q(t): " + String.format("%.3f", qs.queueTime));
            java.lang.System.out.println("Average waiting time:\t" + String.format("%.3f", dn.get(id)));
            java.lang.System.out.println("Average queue size:\t" + String.format("%.3f", qn.get(id)));
            java.lang.System.out.println("Avg arrival interval:\t" + String.format("%.3f", avgArrivalInterval.get(id)));
            java.lang.System.out.println("Rho (load): "); //TODO: write (or not) load for each queue
            java.lang.System.out.println("-----------------------------------------------");
        });
        java.lang.System.out.println("B(t): " + String.format("%.3f", serverBusyTime));
        java.lang.System.out.println("Average load: " + String.format("%.3f", un));
        java.lang.System.out.println("-----------------------------------------------");
    }
}
