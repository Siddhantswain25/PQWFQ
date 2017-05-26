import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private Event event;

    @BeforeEach
    void setUp() {
         event = new Event(EventType.ARRIVAL, 0.0);
    }

    @AfterEach
    void tearDown() {
        event = null;
    }

    @Test
    void getEventType() {
        assertEquals(EventType.ARRIVAL, event.getEventType());
    }

    @Test
    void getTime() {
        assertEquals(0.0, event.getTime());
    }

}