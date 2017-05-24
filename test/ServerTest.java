import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server();
    }

    @AfterEach
    void tearDown() {
        server = null;
    }

    @Test
    void addClient() {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 0.4);
        server.addClient(1, new Packet(0.0, 0.0));
        assertFalse(server.isQueueEmpty(1));
    }

    @Test
    void handleNextClient() {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 0.4);
        server.addClient(1, new Packet(0.0, 0.0));
        server.addClient(1, new Packet(1.0, 1.0));
        server.addClient(1, new Packet(2.0, 2.0));
        server.addClient(1, new Packet(3.0, 3.0));
        server.addClient(1, new Packet(4.0, 4.0));
        server.addClient(1, new Packet(5.0, 5.0));

        assertFalse(server.isQueueEmpty(1));
        for(int i = 0; i < 6; i++) {
            assertEquals((double)i, server.handleNextClient(1).getArrivalTime());
        }
        assertTrue(server.isQueueEmpty(1));
    }

    @Test
    void isHighPriorityQueueHandledFirst() {
        server.addQueue(1, QueuePQWFQ.HIGH_PRIORITY, 1);
        server.addQueue(2, QueuePQWFQ.LOW_PRIORITY, 1);
        server.addClient(2, new Packet(0.0, 1.0));
        server.addClient(2, new Packet(1.0, 0.0));
        server.addClient(2, new Packet(2.0, 2.0));
        server.addClient(1, new Packet(3.0, 4.0));
        server.addClient(1, new Packet(4.0, 3.0));
        server.addClient(1, new Packet(5.0, 5.0));

        double[] expectedOrder = new double[] {4.0, 3.0, 5.0, 1.0, 0.0, 2.0};
        for(int i = 0; i < expectedOrder.length; i++) {
            double actual = server.handleNextClient(server.pqwfqDepartureAlgorithm()).getArrivalTime();
            assertEquals(expectedOrder[i], actual);
        }

    }
}