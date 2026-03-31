package config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

public class WindowStateTest {

    private WindowState windowState;
    private JFrame testFrame;
    private JInternalFrame testWindow;

    @BeforeEach
    public void setUp() {
        windowState = WindowState.getInstance();
        testFrame = new JFrame();
        testWindow = new JInternalFrame("Тестовое окно", true, true, true, true);
        testWindow.setSize(400, 300);
        testWindow.setLocation(100, 100);

        // Очищаем сохранённое состояние перед каждым тестом
        ConfigManager.saveProperties(new java.util.Properties());
    }

    @Test
    public void testSaveAndLoadWindowState() {
        windowState.registerWindows(testFrame, testWindow, null);

        boolean saved = windowState.save();
        assertTrue(saved);

        testWindow.setLocation(200, 200);
        testWindow.setSize(500, 400);

        boolean loaded = windowState.load();
        assertTrue(loaded);

        assertEquals(100, testWindow.getX());
        assertEquals(100, testWindow.getY());
        assertEquals(400, testWindow.getWidth());
        assertEquals(300, testWindow.getHeight());
    }

    @Test
    public void testSaveAndLoadMainFrameState() {
        testFrame.setBounds(50, 50, 1024, 768);
        windowState.registerWindows(testFrame, null, null);

        boolean saved = windowState.save();
        assertTrue(saved);

        testFrame.setBounds(200, 200, 800, 600);

        boolean loaded = windowState.load();
        assertTrue(loaded);

        assertEquals(50, testFrame.getX());
        assertEquals(50, testFrame.getY());
        assertEquals(1024, testFrame.getWidth());
        assertEquals(768, testFrame.getHeight());
    }

    @Test
    public void testLoadWithoutSave() {
        // Очищаем перед тестом
        windowState.registerWindows(testFrame, testWindow, null);
        windowState.save(); // сохраняем текущее

        // Удаляем сохранённые данные
        ConfigManager.saveProperties(new java.util.Properties());
        boolean loaded = windowState.load();
        assertFalse(loaded);
    }
}