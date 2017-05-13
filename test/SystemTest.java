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
    void initialize() {
        assertFalse(system.isEventListEmpty());
    }

}