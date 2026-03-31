package config;

import java.awt.Color;
import java.io.*;
import java.util.Properties;

public class RobotColor {
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.robots";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";

    // Цвет робота
    private static Color robotColor = Color.MAGENTA;

    // Цвет цели
    private static Color targetColor = Color.GREEN;

    // Цвет следа
    private static Color trailColor = new Color(100, 100, 100, 100);

    public enum ColorPreset {
        RED(Color.RED, "red"),
        BLUE(Color.BLUE, "blue"),
        GREEN(Color.GREEN, "green"),
        YELLOW(Color.YELLOW, "yellow"),
        PURPLE(Color.MAGENTA, "purple"),
        GRAY(Color.GRAY, "gray");

        public final Color color;
        public final String key;

        ColorPreset(Color color, String key) {
            this.color = color;
            this.key = key;
        }
    }

    public static void setRobotColor(ColorPreset preset) {
        robotColor = preset.color;
        saveColor("robot.color", preset.key);
    }

    public static Color getRobotColor() {
        return robotColor;
    }

    public static void setTargetColor(ColorPreset preset) {
        targetColor = preset.color;
        saveColor("target.color", preset.key);
    }

    public static Color getTargetColor() {
        return targetColor;
    }

    public static void setTrailColor(ColorPreset preset) {
        // Для серого делаем полупрозрачным
        if (preset == ColorPreset.GRAY) {
            trailColor = new Color(100, 100, 100, 100);
        } else {
            trailColor = preset.color;
        }
        saveColor("trail.color", preset.key);
    }

    public static Color getTrailColor() {
        return trailColor;
    }

    public static void loadColors() {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);

                // Загружаем цвет робота
                String savedRobotColor = props.getProperty("robot.color", "purple");
                for (ColorPreset preset : ColorPreset.values()) {
                    if (preset.key.equals(savedRobotColor)) {
                        robotColor = preset.color;
                        break;
                    }
                }

                // Загружаем цвет цели
                String savedTargetColor = props.getProperty("target.color", "green");
                for (ColorPreset preset : ColorPreset.values()) {
                    if (preset.key.equals(savedTargetColor)) {
                        targetColor = preset.color;
                        break;
                    }
                }

                // Загружаем цвет следа
                String savedTrailColor = props.getProperty("trail.color", "gray");
                for (ColorPreset preset : ColorPreset.values()) {
                    if (preset.key.equals(savedTrailColor)) {
                        if (preset == ColorPreset.GRAY) {
                            trailColor = new Color(100, 100, 100, 100);
                        } else {
                            trailColor = preset.color;
                        }
                        break;
                    }
                }

            } catch (IOException e) {
            }
        }
    }

    private static void saveColor(String key, String colorKey) {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create config directory: " + CONFIG_DIR);
                    return;
                }
            }

            Properties props = new Properties();
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                }
            }

            props.setProperty(key, colorKey);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                props.store(fos, "Robot application settings");
            }
        } catch (IOException e) {
            System.err.println("Failed to save color: " + e.getMessage());
        }
    }
}