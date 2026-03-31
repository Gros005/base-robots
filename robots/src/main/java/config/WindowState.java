package config;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 * Сохраняет и восстанавливает состояние окон приложения.
 * Файл конфигурации хранится в: C:\Users\ИмяПользователя\.robots\config.properties
 */
public class WindowState {

    // Путь к файлу конфигурации в домашней папке пользователя
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.robots";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";

    /**
     * Сохраняет состояние внутреннего окна
     */
    public static void saveWindowState(String windowName, JInternalFrame window) {
        Properties props = loadProperties();

        // Координаты и размеры
        props.setProperty(windowName + ".x", String.valueOf(window.getX()));
        props.setProperty(windowName + ".y", String.valueOf(window.getY()));
        props.setProperty(windowName + ".width", String.valueOf(window.getWidth()));
        props.setProperty(windowName + ".height", String.valueOf(window.getHeight()));

        // Состояние
        props.setProperty(windowName + ".maximized", String.valueOf(window.isMaximum()));
        props.setProperty(windowName + ".iconified", String.valueOf(window.isIcon()));

        saveProperties(props);
        System.out.println("[Config] Saved: " + windowName);
    }

    /**
     * Сохраняет состояние главного окна
     */
    public static void saveWindowState(String windowName, JFrame frame) {
        Properties props = loadProperties();

        props.setProperty(windowName + ".x", String.valueOf(frame.getX()));
        props.setProperty(windowName + ".y", String.valueOf(frame.getY()));
        props.setProperty(windowName + ".width", String.valueOf(frame.getWidth()));
        props.setProperty(windowName + ".height", String.valueOf(frame.getHeight()));

        boolean maximized = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        props.setProperty(windowName + ".maximized", String.valueOf(maximized));

        saveProperties(props);
        System.out.println("[Config] Saved: " + windowName);
    }

    /**
     * Восстанавливает состояние внутреннего окна
     */
    public static void restoreWindowState(String windowName, JInternalFrame window) {
        Properties props = loadProperties();

        try {
            int x = Integer.parseInt(props.getProperty(windowName + ".x", "100"));
            int y = Integer.parseInt(props.getProperty(windowName + ".y", "100"));
            int width = Integer.parseInt(props.getProperty(windowName + ".width", "400"));
            int height = Integer.parseInt(props.getProperty(windowName + ".height", "400"));
            boolean maximized = Boolean.parseBoolean(props.getProperty(windowName + ".maximized", "false"));
            boolean iconified = Boolean.parseBoolean(props.getProperty(windowName + ".iconified", "false"));

            window.setBounds(x, y, width, height);

            if (maximized) {
                try {
                    window.setMaximum(true);
                } catch (java.beans.PropertyVetoException e) {
                }
            }

            if (iconified) {
                try {
                    window.setIcon(true);
                } catch (java.beans.PropertyVetoException e) {
                }
            }

            System.out.println("[Config] Restored: " + windowName);

        } catch (NumberFormatException e) {
            System.out.println("[Config] No saved state for: " + windowName);
        }
    }

    /**
     * Восстанавливает состояние главного окна
     */
    public static void restoreWindowState(String windowName, JFrame frame) {
        Properties props = loadProperties();

        try {
            int x = Integer.parseInt(props.getProperty(windowName + ".x", "50"));
            int y = Integer.parseInt(props.getProperty(windowName + ".y", "50"));
            int width = Integer.parseInt(props.getProperty(windowName + ".width", "1200"));
            int height = Integer.parseInt(props.getProperty(windowName + ".height", "800"));
            boolean maximized = Boolean.parseBoolean(props.getProperty(windowName + ".maximized", "false"));

            frame.setBounds(x, y, width, height);

            if (maximized) {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            System.out.println("[Config] Restored: " + windowName);

        } catch (NumberFormatException e) {
            System.out.println("[Config] No saved state for: " + windowName);
        }
    }

    /**
     * Загружает свойства из файла
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                System.out.println("[Config] Loaded from: " + CONFIG_FILE);
            } catch (IOException e) {
                System.err.println("[Config] Failed to load: " + e.getMessage());
            }
        } else {
            System.out.println("[Config] No config file, will create at: " + CONFIG_FILE);
        }
        return props;
    }

    /**
     * Сохраняет свойства в файл
     */
    private static void saveProperties(Properties props) {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create config directory: " + CONFIG_DIR);
                    return;
                }
                System.out.println("[Config] Created directory: " + CONFIG_DIR);
            }

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Robot application window state");
                System.out.println("[Config] Saved to: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.err.println("[Config] Failed to save: " + e.getMessage());
        }
    }
}