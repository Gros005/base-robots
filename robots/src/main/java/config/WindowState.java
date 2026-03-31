package config;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * Управляет состоянием окон приложения.
 */
public class WindowState implements Saveable {

    private static WindowState instance;

    private JFrame mainFrame;
    private JInternalFrame gameWindow;
    private JInternalFrame logWindow;

    private WindowState() {
        // приватный конструктор
    }

    public static WindowState getInstance() {
        if (instance == null) {
            instance = new WindowState();
        }
        return instance;
    }

    public void registerWindows(JFrame mainFrame, JInternalFrame gameWindow, JInternalFrame logWindow) {
        this.mainFrame = mainFrame;
        this.gameWindow = gameWindow;
        this.logWindow = logWindow;
    }

    @Override
    public boolean save() {
        if (mainFrame == null) {
            return false;
        }

        Properties props = ConfigManager.loadProperties();

        // Сохраняем главное окно
        props.setProperty("main.x", String.valueOf(mainFrame.getX()));
        props.setProperty("main.y", String.valueOf(mainFrame.getY()));
        props.setProperty("main.width", String.valueOf(mainFrame.getWidth()));
        props.setProperty("main.height", String.valueOf(mainFrame.getHeight()));
        props.setProperty("main.maximized", String.valueOf(
                (mainFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0
        ));

        // Сохраняем игровое окно
        if (gameWindow != null) {
            props.setProperty("gameWindow.x", String.valueOf(gameWindow.getX()));
            props.setProperty("gameWindow.y", String.valueOf(gameWindow.getY()));
            props.setProperty("gameWindow.width", String.valueOf(gameWindow.getWidth()));
            props.setProperty("gameWindow.height", String.valueOf(gameWindow.getHeight()));
            props.setProperty("gameWindow.maximized", String.valueOf(gameWindow.isMaximum()));
            props.setProperty("gameWindow.iconified", String.valueOf(gameWindow.isIcon()));
        }

        // Сохраняем окно логов
        if (logWindow != null) {
            props.setProperty("logWindow.x", String.valueOf(logWindow.getX()));
            props.setProperty("logWindow.y", String.valueOf(logWindow.getY()));
            props.setProperty("logWindow.width", String.valueOf(logWindow.getWidth()));
            props.setProperty("logWindow.height", String.valueOf(logWindow.getHeight()));
            props.setProperty("logWindow.maximized", String.valueOf(logWindow.isMaximum()));
            props.setProperty("logWindow.iconified", String.valueOf(logWindow.isIcon()));
        }

        return ConfigManager.saveProperties(props);
    }

    @Override
    public boolean load() {
        Properties props = ConfigManager.loadProperties();

        if (props.isEmpty()) {
            System.out.println("[WindowState] No saved state, using defaults");
            return false;
        }

        try {
            // Восстанавливаем главное окно
            if (mainFrame != null) {
                int x = Integer.parseInt(props.getProperty("main.x", "50"));
                int y = Integer.parseInt(props.getProperty("main.y", "50"));
                int width = Integer.parseInt(props.getProperty("main.width", "1200"));
                int height = Integer.parseInt(props.getProperty("main.height", "800"));
                boolean maximized = Boolean.parseBoolean(props.getProperty("main.maximized", "false"));

                mainFrame.setBounds(x, y, width, height);
                if (maximized) {
                    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            }

            // Восстанавливаем игровое окно
            if (gameWindow != null) {
                int x = Integer.parseInt(props.getProperty("gameWindow.x", "100"));
                int y = Integer.parseInt(props.getProperty("gameWindow.y", "100"));
                int width = Integer.parseInt(props.getProperty("gameWindow.width", "400"));
                int height = Integer.parseInt(props.getProperty("gameWindow.height", "400"));
                boolean maximized = Boolean.parseBoolean(props.getProperty("gameWindow.maximized", "false"));
                boolean iconified = Boolean.parseBoolean(props.getProperty("gameWindow.iconified", "false"));

                gameWindow.setBounds(x, y, width, height);
                if (maximized) {
                    gameWindow.setMaximum(true);
                }
                if (iconified) {
                    gameWindow.setIcon(true);
                }
            }

            // Восстанавливаем окно логов
            if (logWindow != null) {
                int x = Integer.parseInt(props.getProperty("logWindow.x", "550"));
                int y = Integer.parseInt(props.getProperty("logWindow.y", "100"));
                int width = Integer.parseInt(props.getProperty("logWindow.width", "300"));
                int height = Integer.parseInt(props.getProperty("logWindow.height", "800"));
                boolean maximized = Boolean.parseBoolean(props.getProperty("logWindow.maximized", "false"));
                boolean iconified = Boolean.parseBoolean(props.getProperty("logWindow.iconified", "false"));

                logWindow.setBounds(x, y, width, height);
                if (maximized) {
                    logWindow.setMaximum(true);
                }
                if (iconified) {
                    logWindow.setIcon(true);
                }
            }

            System.out.println("[WindowState] Loaded successfully");
            return true;

        } catch (NumberFormatException | java.beans.PropertyVetoException e) {
            System.err.println("[WindowState] Failed to load: " + e.getMessage());
            return false;
        }
    }
}