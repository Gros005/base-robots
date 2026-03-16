package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class WindowStateManager {
    private static final String DIR_NAME = ".robots";
    private static final String FILE_NAME = "window-state.properties";
    private static final String MAIN_PREFIX = "main";
    private static final String IF_PREFIX = "if.";

    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;

    private final Properties properties = new Properties();

    public WindowStateManager() {
        load();
    }

    private void load() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                properties.load(is);
            } catch (IOException ignored) {}
        }
    }

    public void save(JFrame mainFrame, JInternalFrame... internalFrames) {
        storeComponent(MAIN_PREFIX, mainFrame);
        properties.setProperty(MAIN_PREFIX + ".state", String.valueOf(mainFrame.getExtendedState()));

        for (JInternalFrame f : internalFrames) {
            if (f == null) continue;
            String key = getInternalKey(f);
            storeComponent(key, f);
            properties.setProperty(key + ".icon", String.valueOf(f.isIcon()));
            properties.setProperty(key + ".max", String.valueOf(f.isMaximum()));

            properties.setProperty(key + ".focused", String.valueOf(f.isSelected()));
        }
        write();
    }

    public void restore(JFrame mainFrame, JInternalFrame... internalFrames) {
        // 1. Главное окно
        restoreComponent(MAIN_PREFIX, mainFrame);
        Integer state = getInt(MAIN_PREFIX + ".state");
        if (state != null) mainFrame.setExtendedState(state);

        // 2. Внутренние окна
        for (JInternalFrame f : internalFrames) {
            if (f == null) continue;
            String key = getInternalKey(f);
            restoreComponent(key, f);

            try {
                if (getBool(key + ".max")) f.setMaximum(true);
                if (getBool(key + ".icon")) f.setIcon(true);

                // Если окно было в фокусе, вытаскиваем на передний план
                if (getBool(key + ".focused")) {
                    f.toFront();
                    f.setSelected(true);
                }
            } catch (Exception ignored) {}
        }
    }

    private void storeComponent(String prefix, Component c) {
        Rectangle b = c.getBounds();
        properties.setProperty(prefix + ".x", String.valueOf(b.x));
        properties.setProperty(prefix + ".y", String.valueOf(b.y));
        properties.setProperty(prefix + ".w", String.valueOf(b.width));
        properties.setProperty(prefix + ".h", String.valueOf(b.height));
    }

    private void restoreComponent(String prefix, Component c) {
        Integer x = getInt(prefix + ".x");
        Integer y = getInt(prefix + ".y");
        Integer w = getInt(prefix + ".w");
        Integer h = getInt(prefix + ".h");

        if (x != null && y != null && w != null && h != null) {
            if (w >= MIN_WIDTH && h >= MIN_HEIGHT) {
                c.setBounds(x, y, w, h);
            }
        }
    }

    private void write() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream os = Files.newOutputStream(path)) {
                properties.store(os, "Window States");
            }
        } catch (IOException ignored) {}
    }

    private Path getConfigPath() {
        return Path.of(System.getProperty("user.home"), DIR_NAME, FILE_NAME);
    }

    private String getInternalKey(JInternalFrame f) {
        String name = f.getName();
        return IF_PREFIX + ((name != null && !name.isBlank()) ? name : f.getClass().getSimpleName());
    }

    private Integer getInt(String key) {
        try { return Integer.parseInt(properties.getProperty(key)); } catch (Exception e) { return null; }
    }

    private boolean getBool(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
}