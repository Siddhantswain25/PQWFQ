import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClockTest {
    @AfterEach
    void tearDown() {
        Clock.reset();
    }

    @Test
    void getCurrentTime() {
        assertEquals(0.0, Clock.getCurrentTime());
    }

    @Test
    void incrementTime() {
        Clock.setTime(1.0);
        assertEquals(1.0, Clock.getCurrentTime());
    }

}