package gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WindowStateManagerTest {
    private WindowStateManager manager;
    private JFrame mainFrame;
    private JInternalFrame logFrame;
    private JInternalFrame gameFrame;
    private JDesktopPane desktopPane;
    private Path testConfigPath;

    @BeforeEach
    public void setUp() {
        testConfigPath = Path.of(System.getProperty("user.home"), ".robots", "window-state.properties");
        manager = new WindowStateManager(testConfigPath);
        mainFrame = new JFrame();
        desktopPane = new JDesktopPane();
        mainFrame.setContentPane(desktopPane);

        logFrame = new JInternalFrame();
        logFrame.setName("logWindow");

        gameFrame = new JInternalFrame();
        gameFrame.setName("gameWindow");

        desktopPane.add(logFrame);
        desktopPane.add(gameFrame);
    }

    @AfterEach
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
        logFrame.setBounds(100, 100, 200, 200);
        logFrame.setMaximum(true);

        manager.save(mainFrame, logFrame);

        logFrame.setMaximum(false);

        manager.restore(mainFrame, logFrame);

        assertTrue(logFrame.isMaximum());
    }

    @Test
    public void testIconifiedStatePersistence() throws Exception {
        logFrame.setIcon(true);

        manager.save(mainFrame, logFrame);

        logFrame.setIcon(false);
        manager.restore(mainFrame, logFrame);

        assertTrue(logFrame.isIcon());
    }

    @Test
    public void testMainFrameExtendedStateDoesNotRestoreIconifiedFlag() throws IOException {
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH | Frame.ICONIFIED);

        manager.save(mainFrame, logFrame);

        Properties properties = new Properties();
        properties.load(Files.newInputStream(testConfigPath));

        assertEquals(String.valueOf(Frame.MAXIMIZED_BOTH), properties.getProperty("main.extendedState"));

        mainFrame.setExtendedState(Frame.NORMAL);
        manager.restore(mainFrame, logFrame);

        assertEquals(Frame.MAXIMIZED_BOTH, mainFrame.getExtendedState());
        assertFalse((mainFrame.getExtendedState() & Frame.ICONIFIED) != 0);
    }

    @Test
    public void testSaveKeepsAllNamedWindowsSeparately() throws IOException {
        JInternalFrame secondGameFrame = new JInternalFrame();
        secondGameFrame.setName("gameWindow-2");
        secondGameFrame.setBounds(700, 150, 420, 420);
        desktopPane.add(secondGameFrame);

        manager.save(mainFrame, logFrame, gameFrame, secondGameFrame);

        Properties properties = new Properties();
        properties.load(Files.newInputStream(testConfigPath));

        assertEquals("700", properties.getProperty("internal.gameWindow-2.x"));
        assertEquals("420", properties.getProperty("internal.gameWindow-2.width"));
    }

    @Test
    public void testRestorePreservesSavedZOrder() {
        desktopPane.setComponentZOrder(logFrame, 0);
        desktopPane.setComponentZOrder(gameFrame, 1);

        manager.save(mainFrame, logFrame, gameFrame);

        desktopPane.setComponentZOrder(logFrame, 1);
        desktopPane.setComponentZOrder(gameFrame, 0);

        manager.restore(mainFrame, logFrame, gameFrame);

        assertEquals(0, desktopPane.getComponentZOrder(logFrame));
        assertEquals(1, desktopPane.getComponentZOrder(gameFrame));
    }
}
