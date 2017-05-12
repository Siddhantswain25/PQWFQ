import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemTest {

    System system;

    @BeforeEach
    void setUp() {
        system =  new System();
    }

    @AfterEach
    void tearDown() {
        system = null;
    }

    @Test
    void addEvent() {
        system.addEvent(new Event(EventType.ARRIVAL, 0.0));
        assertFalse(system.isEventListEmpty());
    }

    @Test
    void processNextEvent() {
        system.addEvent(new Event(EventType.ARRIVAL, 0.0));
        assertFalse(system.isEventListEmpty());
        system.processNextEvent();
        assertEquals(EventType.DEPARTURE, system.getNextEventType());
        system.processNextEvent();
        assertTrue(system.isEventListEmpty());
    }

}