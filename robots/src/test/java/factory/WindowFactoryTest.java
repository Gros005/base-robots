package factory;

import gui.GameWindow;
import gui.LogWindow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WindowFactoryTest {

    @Test
    public void testCreateGameWindow() {
        GameWindow window = WindowFactory.createGameWindow();

        assertNotNull(window);
        assertEquals(400, window.getWidth());
        assertEquals(400, window.getHeight());
        assertFalse(window.isVisible());
        // После создания окно переключается в обычный режим
        assertEquals("Игровое поле (обычный режим)", window.getTitle());
    }

    @Test
    public void testCreateLogWindow() {
        LogWindow window = WindowFactory.createLogWindow();

        assertNotNull(window);
        assertTrue(window.getWidth() > 0);
        assertTrue(window.getHeight() > 0);
        assertEquals("Протокол работы", window.getTitle());
        assertTrue(window.isResizable());
    }
}