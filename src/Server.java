import java.util.PriorityQueue;

public class Server {
    private boolean isBusy;
    private PriorityQueue<Double> queue; //TODO: add more queues later!

    Server() {
        queue = new PriorityQueue<>();
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void addClient(double arrivalTime) {
        queue.add(arrivalTime);
    }

    public double handleNextClient() {
        return queue.poll();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public int getQueueSize() {
        return queue.size();
    }
}
