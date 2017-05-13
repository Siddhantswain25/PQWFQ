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
        server.addClient(0.0);
        assertFalse(server.isQueueEmpty());
    }

    @Test
    void handleNextClient() {
        server.addClient(0.0);
        server.addClient(1.0);
        server.addClient(2.0);
        server.addClient(3.0);
        server.addClient(4.0);
        server.addClient(5.0);

        assertFalse(server.isQueueEmpty());
        for(int i = 0; i < 6; i++) {
            assertEquals((double)i, server.handleNextClient());
        }
        assertTrue(server.isQueueEmpty());
    }
}