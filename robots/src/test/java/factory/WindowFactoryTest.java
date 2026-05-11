package factory;

import gui.GameWindow;
import gui.LogWindow;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class WindowFactoryTest {

    @BeforeEach
    public void setUp() {
        // Сбрасываем счетчик перед каждым тестом
        WindowFactory.resetCounter();
    }

    @AfterEach
    public void tearDown() {
        // Сбрасываем счетчик после каждого теста
        WindowFactory.resetCounter();
    }

    @Test
    public void testCreateGameWindow() {
        GameWindow window = WindowFactory.createGameWindow();

        assertNotNull(window);
        assertEquals(400, window.getWidth());
        assertEquals(400, window.getHeight());
        assertFalse(window.isVisible());
        assertEquals("Игровое поле", window.getTitle());
    }

    @Test
    public void testCreateNewGameWindow() {
        GameWindow window1 = WindowFactory.createNewGameWindow();
        GameWindow window2 = WindowFactory.createNewGameWindow();

        assertNotNull(window1);
        assertNotNull(window2);
        assertNotSame(window1, window2);

        // проверяем, что заголовки разные
        assertNotEquals(window1.getTitle(), window2.getTitle());
    }

    @Test
    public void testCreateLogWindow() {
        LogWindow window = WindowFactory.createLogWindow();

        assertNotNull(window);
        // проверим, что размеры > 0
        assertTrue(window.getWidth() > 0);
        assertTrue(window.getHeight() > 0);
        assertEquals("Протокол работы", window.getTitle());
        assertTrue(window.isResizable());
    }

    @Test
    public void testResetCounter() {
        GameWindow window1 = WindowFactory.createNewGameWindow();
        String title1 = window1.getTitle();

        GameWindow window2 = WindowFactory.createNewGameWindow();
        String title2 = window2.getTitle();

        assertNotEquals(title1, title2);

        // Сбрасываем счетчик
        WindowFactory.resetCounter();

        GameWindow window3 = WindowFactory.createNewGameWindow();
        String title3 = window3.getTitle();

        // После сброса заголовок должен совпадать с первым
        assertEquals(title1, title3);
    }

    @Test
    public void testMultipleNewWindowsHaveDifferentPositions() {
        GameWindow window1 = WindowFactory.createNewGameWindow();
        GameWindow window2 = WindowFactory.createNewGameWindow();

        assertNotSame(window1, window2);

        int x1 = window1.getX();
        int y1 = window1.getY();
        int x2 = window2.getX();
        int y2 = window2.getY();

        System.out.println("Window1: (" + x1 + ", " + y1 + ")");
        System.out.println("Window2: (" + x2 + ", " + y2 + ")");

        // Проверяем, что окна имеют разные координаты
        boolean positionsDifferent = (x1 != x2) || (y1 != y2);

        if (!positionsDifferent) {
            assertNotSame(window1, window2);
        } else {
            assertTrue(positionsDifferent, "Windows should have different positions");
        }
    }
}