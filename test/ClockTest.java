import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import components.Clock;

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
    void setTime() {
        Clock.setTime(1.0);
        assertEquals(1.0, Clock.getCurrentTime());
    }

    @Test
    void increaseTime() {
        Clock.increaseTime(5);
        assertEquals(5, Clock.getCurrentTime());
        Clock.increaseTime(5);
        assertEquals(10, Clock.getCurrentTime());
    }
}