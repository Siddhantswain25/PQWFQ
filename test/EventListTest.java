import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventListTest {

    private EventList eventList;

    @BeforeEach
    void setUp() {
        eventList = new EventList();
    }

    @AfterEach
    void tearDown() {
        eventList = null;
    }

    @Test
    void addEvent() {
        eventList.addEvent(new Event(EventType.ARRIVAL, 0.0));
        assertFalse(eventList.isEmpty());
    }

    @Test
    void popNextEvent() {
        eventList.addEvent(new Event(EventType.ARRIVAL, 0.0));
        eventList.popNextEvent();
        assertTrue(eventList.isEmpty());
    }
}