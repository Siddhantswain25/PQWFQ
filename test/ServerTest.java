import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    Server server;

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
        server.handleNextClient();
        assertTrue(server.isQueueEmpty());
    }
}