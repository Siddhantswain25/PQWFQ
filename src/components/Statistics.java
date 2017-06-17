package components;

import java.util.HashMap;

public class Statistics {
    public class QueueStatistics {
        public int numberOfDelays = 0;
        public double totalDelay = 0.0;
        public double queueTime = 0.0;
        public double arrivalInterval = 0.0;
        public long numberOfArrivals = 0;
        public long bytesServiced = 0;
    }
    private long numberOfArrivals = 0;
    private double serverBusyTime = 0.0;
    private long totalBytesServiced = 0;
    private HashMap<Integer, QueueStatistics> queueStatistics = new HashMap<>();

    public void registerQueue(int queueId, int priority) {
        queueStatistics.put(queueId, new QueueStatistics());
        //TODO: use priority
    }

    public void addDelayToStatistics(int queueId, double delay) {
        queueStatistics.get(queueId).totalDelay += delay;
        queueStatistics.get(queueId).numberOfDelays++;
    }

    public long getNumberOfArrivals() {
        return numberOfArrivals;
    }

    public void increaseNumberOfArrivals() {
        this.numberOfArrivals++;
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

    public void increaseBytesServiced(int queueId, int bytes) {
        this.queueStatistics.get(queueId).bytesServiced += bytes;
        this.totalBytesServiced += bytes;
    }

    public void displayAllStatistics() {
        double totalSimTime = Clock.getCurrentTime();

        HashMap<Integer, Double> dn = new HashMap<>();
        HashMap<Integer, Double> qn = new HashMap<>();
        HashMap<Integer, Double> avgArrivalInterval = new HashMap<>();
        HashMap<Integer, Double> bps = new HashMap<>();
        double un = serverBusyTime/totalSimTime;

        queueStatistics.forEach((id, stat) -> {
            dn.put(id, stat.totalDelay/stat.numberOfDelays);
            qn.put(id, stat.queueTime/totalSimTime);
            avgArrivalInterval.put(id, stat.arrivalInterval/(double)stat.numberOfArrivals);
            bps.put(id, ((stat.bytesServiced*serverBusyTime)/(totalBytesServiced*totalSimTime)));
        });

        double sumOfAvgLoads = bps
                .values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

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
            java.lang.System.out.println("Rho (load): " + String.format("%.3f", bps.get(id)));
            java.lang.System.out.println("-----------------------------------------------");
        });
        java.lang.System.out.println("B(t): " + String.format("%.3f", serverBusyTime));
        java.lang.System.out.println("Sum of avg loads: " + String.format("%.3f", sumOfAvgLoads));
        java.lang.System.out.println("Average load: " + String.format("%.3f", un));
        java.lang.System.out.println("-----------------------------------------------");
    }
}
