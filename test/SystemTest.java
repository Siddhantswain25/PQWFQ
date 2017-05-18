import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemTest {

    System system;

    @BeforeEach
    void setUp() {
        Server server = new Server();
        server.addQueue(1, new QueuePQWFQ(QueuePQWFQ.HIGH_PRIORITY, 1.0));
        system =  new System(server);

    }

    @AfterEach
    void tearDown() {
        system = null;
    }

    @Test
    void initialize() {
        assertFalse(system.isEventListEmpty());
    }

}