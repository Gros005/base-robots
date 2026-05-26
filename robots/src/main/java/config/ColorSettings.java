package config;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Управляет цветами приложения (робот, цель, след).
 */
public class ColorSettings implements Saveable {

    private static ColorSettings instance;

    private Color robotColor = Color.MAGENTA;
    private Color targetColor = Color.GREEN;
    private Color trailColor = new Color(100, 100, 100, 100);

    private Color player1Color = Color.RED;
    private Color player2Color = Color.BLUE;

    public enum ColorPreset {
        RED(Color.RED, "red"),
        BLUE(Color.BLUE, "blue"),
        GREEN(Color.GREEN, "green"),
        YELLOW(Color.YELLOW, "yellow"),
        ORANGE(Color.ORANGE, "orange"),
        CYAN(Color.CYAN, "cyan"),
        PINK(Color.PINK, "pink"),
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

    public static List<Color> getRobotColors() {
        return Arrays.stream(ColorPreset.values())
                .filter(preset -> preset != ColorPreset.GRAY)
                .map(preset -> preset.color)
                .collect(Collectors.toList());
    }

    public static int getMaxRobotsCount() {
        return ColorPreset.values().length - 1;
    }

    public static Color getRobotColorByIndex(int index) {
        List<Color> colors = getRobotColors();
        return colors.get(index % colors.size());
    }

    public static String getColorNameByColor(Color c) {
        if (c.equals(Color.RED)) return "Красный";
        if (c.equals(Color.BLUE)) return "Синий";
        if (c.equals(Color.GREEN)) return "Зелёный";
        if (c.equals(Color.YELLOW)) return "Жёлтый";
        if (c.equals(Color.ORANGE)) return "Оранжевый";
        if (c.equals(Color.CYAN)) return "Голубой";
        if (c.equals(Color.PINK)) return "Розовый";
        if (c.equals(Color.MAGENTA)) return "Фиолетовый";
        return "Неизвестный";
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

    public void setPlayer1Color(ColorPreset preset) {
        this.player1Color = preset.color;
        save();
    }

    public Color getPlayer1Color() {
        return player1Color;
    }

    public void setPlayer2Color(ColorPreset preset) {
        this.player2Color = preset.color;
        save();
    }

    public Color getPlayer2Color() {
        return player2Color;
    }
}