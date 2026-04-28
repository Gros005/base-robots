package gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainApplicationFrameRestoreTest {
    private final Path statePath = Path.of(System.getProperty("user.home"), ".robots", "window-state.properties");

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(statePath);
    }

    @Test
    public void testRestoresAdditionalSavedGameWindows() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("main.x", "0");
        properties.setProperty("main.y", "0");
        properties.setProperty("main.width", "800");
        properties.setProperty("main.height", "600");
        properties.setProperty("internal.gameWindow.x", "10");
        properties.setProperty("internal.gameWindow.y", "10");
        properties.setProperty("internal.gameWindow.width", "400");
        properties.setProperty("internal.gameWindow.height", "400");
        properties.setProperty("internal.logWindow.x", "20");
        properties.setProperty("internal.logWindow.y", "20");
        properties.setProperty("internal.logWindow.width", "300");
        properties.setProperty("internal.logWindow.height", "400");
        properties.setProperty("internal.gameWindow-2.x", "30");
        properties.setProperty("internal.gameWindow-2.y", "30");
        properties.setProperty("internal.gameWindow-2.width", "400");
        properties.setProperty("internal.gameWindow-2.height", "400");
        Files.createDirectories(statePath.getParent());
        try (var output = Files.newOutputStream(statePath)) {
            properties.store(output, "test");
        }

        final MainApplicationFrame[] frameRef = new MainApplicationFrame[1];
        SwingUtilities.invokeAndWait(() -> frameRef[0] = new MainApplicationFrame());

        assertEquals(3, frameRef[0].getDesktopPane().getAllFrames().length);

        SwingUtilities.invokeAndWait(() -> frameRef[0].dispose());
    }
}
