import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemTest {

    private System system;
    private int N;
    private double lambda = 0.4;
    private double mi = 0.5;
    private int nominalPacketSizeInBytes = 100;
    private int serverServiceBitrate = 4000; //4kbps = 0.5kBps = 5 packets per second (if packet_size = 100B)

    @BeforeEach
    void setUp() {
        N = 700;
    }

    @AfterEach
    void tearDown() {
        system = null;
        N = 0;
        Clock.reset();
    }

    @Test
    void oneHighPriorityQueue() throws IllegalArgumentException {
        Server server = new Server(serverServiceBitrate);
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.HIGH_PRIORITY, 1.0, nominalPacketSizeInBytes));
        system =  new System(server, lambda, mi);

        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

    @Test
    void twoLowPriorityQueues() throws IllegalArgumentException {
        Server server = new Server(serverServiceBitrate);
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5, nominalPacketSizeInBytes));
        server.addQueue(2, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5, nominalPacketSizeInBytes));
        system =  new System(server, lambda, mi);

        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

    @Test
    void oneHighAndTwoLowPriorityQueues() throws IllegalArgumentException {
        Server server = new Server(serverServiceBitrate);
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5, nominalPacketSizeInBytes));
        server.addQueue(2, new QueuePQWFQ(QueuePQWFQ.LOW_PRIORITY, 0.5, nominalPacketSizeInBytes));
        server.addQueue(3, new QueuePQWFQ(QueuePQWFQ.HIGH_PRIORITY, 1, nominalPacketSizeInBytes));
        system =  new System(server, lambda, mi);

        system.displayTrace();
        while(system.getNumberOfArrivals() < N) {
            system.processNextEvent();
            system.displayTrace();
        }
        system.displayAllStatistics();
    }

}