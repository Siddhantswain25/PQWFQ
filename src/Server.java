import java.util.PriorityQueue;
import java.util.Queue;

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

    public void handleNextClient() {
        queue.poll();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }
}
