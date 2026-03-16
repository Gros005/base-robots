package gui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class WindowStateManagerTest {
    private WindowStateManager manager;
    private JFrame mainFrame;
    private JInternalFrame logFrame;
    private JInternalFrame gameFrame;
    private Path testConfigPath;

    @Before
    public void setUp() {
        manager = new WindowStateManager();
        mainFrame = new JFrame();

        logFrame = new JInternalFrame();
        logFrame.setName("logWindow");

        gameFrame = new JInternalFrame();
        gameFrame.setName("gameWindow");

        testConfigPath = Path.of(System.getProperty("user.home"), ".robots", "window-state.properties");
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(testConfigPath);
        mainFrame.dispose();
        logFrame.dispose();
        gameFrame.dispose();
    }

    @Test
    public void testSaveAndRestoreMultipleWindows() {
        logFrame.setBounds(10, 20, 300, 400);
        gameFrame.setBounds(500, 100, 400, 400);

        manager.save(mainFrame, logFrame, gameFrame);

        logFrame.setBounds(0, 0, 0, 0);
        gameFrame.setBounds(0, 0, 0, 0);

        manager.restore(mainFrame, logFrame, gameFrame);

        assertEquals(10, logFrame.getX());
        assertEquals(300, logFrame.getWidth());
        assertEquals(500, gameFrame.getX());
        assertEquals(400, gameFrame.getHeight());
    }

    @Test
    public void testMaximizedStatePersistence() throws Exception {
        // Имитируем развернутое окно
        logFrame.setBounds(100, 100, 200, 200);
        logFrame.setMaximum(true);

        manager.save(mainFrame, logFrame);

        logFrame.setMaximum(false);

        manager.restore(mainFrame, logFrame);

        assertTrue("Окно должно восстановить состояние 'Maximized'", logFrame.isMaximum());
    }

    @Test
    public void testIconifiedStatePersistence() throws Exception {
        logFrame.setIcon(true);

        manager.save(mainFrame, logFrame);

        logFrame.setIcon(false);
        manager.restore(mainFrame, logFrame);

        assertTrue("Окно должно остаться свернутым", logFrame.isIcon());
    }

    @Test
    public void testMainFrameExtendedState() {
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        manager.save(mainFrame, logFrame);

        mainFrame.setExtendedState(JFrame.NORMAL);
        manager.restore(mainFrame, logFrame);

        assertEquals(JFrame.MAXIMIZED_BOTH, mainFrame.getExtendedState());
    }
}