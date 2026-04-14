package config;

import java.awt.Color;
import java.util.Properties;

/**
 * Управляет цветами приложения (робот, цель, след).
 */
public class ColorSettings implements Saveable {

    private static ColorSettings instance;

    private Color robotColor = Color.MAGENTA;
    private Color targetColor = Color.GREEN;
    private Color trailColor = new Color(100, 100, 100, 100);

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

    private ColorSettings() {
        // приватный конструктор
    }

    public static ColorSettings getInstance() {
        if (instance == null) {
            instance = new ColorSettings();
        }
        return instance;
    }

    public void setRobotColor(ColorPreset preset) {
        this.robotColor = preset.color;
        save();
    }

    public Color getRobotColor() {
        return robotColor;
    }

    public void setTargetColor(ColorPreset preset) {
        this.targetColor = preset.color;
        save();
    }

    public Color getTargetColor() {
        return targetColor;
    }

    public void setTrailColor(ColorPreset preset) {
        if (preset == ColorPreset.GRAY) {
            this.trailColor = new Color(100, 100, 100, 100);
        } else {
            this.trailColor = preset.color;
        }
        save();
    }

    public Color getTrailColor() {
        return trailColor;
    }

    @Override
    public boolean save() {
        Properties props = ConfigManager.loadProperties();

        props.setProperty("robot.color", getKeyFromColor(robotColor));
        props.setProperty("target.color", getKeyFromColor(targetColor));

        if (trailColor.equals(new Color(100, 100, 100, 100))) {
            props.setProperty("trail.color", "gray");
        } else {
            props.setProperty("trail.color", getKeyFromColor(trailColor));
        }

        return ConfigManager.saveProperties(props);
    }

    @Override
    public boolean load() {
        Properties props = ConfigManager.loadProperties();

        if (props.isEmpty()) {
            System.out.println("[ColorSettings] No saved colors, using defaults");
            return false;
        }

        try {
            String savedRobotColor = props.getProperty("robot.color", "purple");
            for (ColorPreset preset : ColorPreset.values()) {
                if (preset.key.equals(savedRobotColor)) {
                    robotColor = preset.color;
                    break;
                }
            }

            String savedTargetColor = props.getProperty("target.color", "green");
            for (ColorPreset preset : ColorPreset.values()) {
                if (preset.key.equals(savedTargetColor)) {
                    targetColor = preset.color;
                    break;
                }
            }

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

            System.out.println("[ColorSettings] Loaded successfully");
            return true;

        } catch (Exception e) {
            System.err.println("[ColorSettings] Failed to load: " + e.getMessage());
            return false;
        }
    }

    private String getKeyFromColor(Color color) {
        for (ColorPreset preset : ColorPreset.values()) {
            if (preset.color.equals(color)) {
                return preset.key;
            }
        }
        return "purple";
    }
}