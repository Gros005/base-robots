package gui;

import factory.WindowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationFrameTest {

    private MainApplicationFrame frame;

    @BeforeEach
    public void setUp() {
        // Создаем фрейм в потоке EDT
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new MainApplicationFrame();
            });
        } catch (Exception e) {
            fail("Could not create frame: " + e.getMessage());
        }
    }

    @Test
    public void testAddWindow() {
        GameWindow testWindow = WindowFactory.createGameWindow();
        int initialCount = frame.getDesktopPane().getAllFrames().length;

        assertDoesNotThrow(() -> {
            frame.addWindow(testWindow);
        });

        // Проверяем, что окно добавилось
        int newCount = frame.getDesktopPane().getAllFrames().length;
        assertEquals(initialCount + 1, newCount);

        // Проверяем, что окно видимо
        assertTrue(testWindow.isVisible());
    }

    @Test
    public void testExitApplicationMethodExists() {
        try {
            Method exitMethod = MainApplicationFrame.class.getDeclaredMethod("exitApplication");
            assertNotNull(exitMethod);
            assertEquals(boolean.class, exitMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("Method exitApplication not found");
        }
    }

    @Test
    public void testLanguageMenuExists() {
        JMenuBar menuBar = frame.getJMenuBar();
        assertNotNull(menuBar);

        boolean foundLanguageMenu = false;
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                String text = menu.getText();
                if (text.equals("Язык") || text.equals("Language")) {
                    foundLanguageMenu = true;
                    break;
                }
            }
        }

        assertTrue(foundLanguageMenu, "Language menu not found");
    }

    @Test
    public void testFileMenuExists() {
        JMenuBar menuBar = frame.getJMenuBar();
        assertNotNull(menuBar);

        boolean foundFileMenu = false;
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && (menu.getText().equals("Файл") || menu.getText().equals("File"))) {
                foundFileMenu = true;

                // Проверяем, что в меню есть пункты New и Quit
                boolean hasNew = false;
                boolean hasQuit = false;

                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null) {
                        String itemText = item.getText();
                        if (itemText.equals("Новый") || itemText.equals("New")) {
                            hasNew = true;
                        }
                        if (itemText.equals("Выход") || itemText.equals("Quit")) {
                            hasQuit = true;
                        }
                    }
                }

                assertTrue(hasNew, "File menu should have New item");
                assertTrue(hasQuit, "File menu should have Quit item");
                break;
            }
        }

        assertTrue(foundFileMenu, "File menu not found");
    }

    @Test
    public void testWindowsMenuExists() {
        JMenuBar menuBar = frame.getJMenuBar();
        assertNotNull(menuBar);

        boolean foundWindowsMenu = false;
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && menu.getText().equals("Окна")) {
                foundWindowsMenu = true;

                // Проверяем, что есть пункт "Закрыть все"
                boolean hasCloseAll = false;
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null && item.getText().equals("Закрыть все")) {
                        hasCloseAll = true;
                        break;
                    }
                }
                assertTrue(hasCloseAll, "Windows menu should have 'Close all' item");
                break;
            }
        }

        assertTrue(foundWindowsMenu, "Windows menu not found");
    }

    @Test
    public void testDesktopPaneExists() {
        assertNotNull(frame.getDesktopPane());
        assertTrue(frame.getDesktopPane() instanceof JDesktopPane);
    }
}