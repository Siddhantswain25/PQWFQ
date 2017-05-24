import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SystemTest {

    private System system;

    @AfterEach
    void tearDown() {
        system = null;
    }

    @Test
    void oneHighPriorityQueue() {
        Server server = new Server();
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.HIGH_PRIORITY, 1.0));
        system =  new System(server, 0.4, 0.5);

        int N = 70000;
        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

    @Test
    void twoLowPriorityQueues() {
        Server server = new Server();
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5));

        server.addQueue(2, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5));
        system =  new System(server, 0.4, 0.5);

        int N = 7000;
        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

    @Test
    void oneHighAndTwoLowPriorityQueues() {
        Server server = new Server();
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5));
        server.addQueue(2, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5));
        server.addQueue(3, new QueuePQWFQ(QueuePQWFQ.HIGH_PRIORITY, 1));
        system =  new System(server, 0.4, 0.5);

        int N = 7000;
        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

}