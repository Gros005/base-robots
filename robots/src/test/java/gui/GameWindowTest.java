package gui;

import factory.WindowFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameWindowTest {

    @Test
    public void testGameWindowCreation() {
        GameWindow gameWindow = WindowFactory.createGameWindow();
        assertNotNull(gameWindow);
        assertEquals("Игровое поле (обычный режим)", gameWindow.getTitle());
    }

    @Test
    public void testGameWindowIsResizable() {
        GameWindow gameWindow = WindowFactory.createGameWindow();
        assertTrue(gameWindow.isResizable());
    }

    @Test
    public void testGameWindowSwitching() {
        GameWindow gameWindow = WindowFactory.createGameWindow();

        gameWindow.switchToSingleMode();
        assertEquals("Игровое поле (обычный режим)", gameWindow.getTitle());

        assertNotNull(gameWindow.getSinglePanel());
    }
}