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
    @Test
    void testRestoreWindowGeometry() throws IOException {
        Properties props = new Properties();
        props.setProperty("main.x", "200");
        props.setProperty("main.y", "200");
        props.setProperty("main.w", "1000");
        props.setProperty("main.h", "900");

        Path configPath = WindowStateManager.getConfigPath();
        Files.createDirectories(configPath.getParent());
        try (java.io.OutputStream os = Files.newOutputStream(configPath)) {
            props.store(os, null);
        }

        mainFrame = new MainApplicationFrame();

        assertEquals(1000, mainFrame.getWidth(), "Ширина не восстановилась из файла!");
        assertEquals(900, mainFrame.getHeight(), "Высота не восстановилась из файла!");
        assertEquals(200, mainFrame.getX(), "Координата X не восстановилась!");
    }

    @Test
    void testSaveMaximizedState() {
        mainFrame = new MainApplicationFrame();

        mainFrame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);


        WindowStateManager.save(mainFrame);

        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(WindowStateManager.getConfigPath())) {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(String.valueOf(javax.swing.JFrame.MAXIMIZED_BOTH),
                props.getProperty("main.extendedState"),
                "Состояние 'Развернуто на весь экран' не сохранилось!");
    }

    @AfterEach
    void tearDown() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }
}