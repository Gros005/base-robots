package gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainFrameTest {
    private MainApplicationFrame mainFrame;

    @BeforeEach
    void setUp() {
        // Очищаем файл перед тестом, чтобы старые запуски не мешали
        Path configPath = WindowStateManager.getConfigPath();
        try {
            Files.deleteIfExists(configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testWindowGeometrySaving() throws IOException {
        mainFrame = new MainApplicationFrame();

        int expectedWidth = 1234;
        int expectedHeight = 900;

        mainFrame.setSize(expectedWidth, expectedHeight);

        mainFrame.validate();

        WindowStateManager.save(mainFrame);

        Properties props = new Properties();
        Path configPath = WindowStateManager.getConfigPath();
        try (InputStream is = Files.newInputStream(configPath)) {
            props.load(is);
        }

        assertEquals(String.valueOf(expectedWidth), props.getProperty("main.w"), "Ширина не совпала!");
        assertEquals(String.valueOf(expectedHeight), props.getProperty("main.h"), "Высота не совпала!");
    }

    @AfterEach
    void tearDown() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }
}