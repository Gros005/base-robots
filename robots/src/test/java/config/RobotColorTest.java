package config;

import org.junit.jupiter.api.Test;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

public class RobotColorTest {

    @Test
    public void testRobotColorPresets() {
        // Проверяем, что предустановки соответствуют цветам
        assertEquals(Color.RED, RobotColor.ColorPreset.RED.color);
        assertEquals(Color.BLUE, RobotColor.ColorPreset.BLUE.color);
        assertEquals(Color.GREEN, RobotColor.ColorPreset.GREEN.color);
        assertEquals(Color.YELLOW, RobotColor.ColorPreset.YELLOW.color);
        assertEquals(Color.MAGENTA, RobotColor.ColorPreset.PURPLE.color);
        assertEquals(Color.GRAY, RobotColor.ColorPreset.GRAY.color);
    }

    @Test
    public void testRobotColorPresetKeys() {
        assertEquals("red", RobotColor.ColorPreset.RED.key);
        assertEquals("blue", RobotColor.ColorPreset.BLUE.key);
        assertEquals("green", RobotColor.ColorPreset.GREEN.key);
        assertEquals("yellow", RobotColor.ColorPreset.YELLOW.key);
        assertEquals("purple", RobotColor.ColorPreset.PURPLE.key);
        assertEquals("gray", RobotColor.ColorPreset.GRAY.key);
    }

    @Test
    public void testSetAndGetRobotColor() {
        RobotColor.setRobotColor(RobotColor.ColorPreset.RED);
        assertEquals(Color.RED, RobotColor.getRobotColor());

        RobotColor.setRobotColor(RobotColor.ColorPreset.BLUE);
        assertEquals(Color.BLUE, RobotColor.getRobotColor());
    }

    @Test
    public void testSetAndGetTargetColor() {
        RobotColor.setTargetColor(RobotColor.ColorPreset.GREEN);
        assertEquals(Color.GREEN, RobotColor.getTargetColor());

        RobotColor.setTargetColor(RobotColor.ColorPreset.YELLOW);
        assertEquals(Color.YELLOW, RobotColor.getTargetColor());
    }

    @Test
    public void testSetAndGetTrailColor() {
        RobotColor.setTrailColor(RobotColor.ColorPreset.RED);
        assertEquals(Color.RED, RobotColor.getTrailColor());

        RobotColor.setTrailColor(RobotColor.ColorPreset.BLUE);
        assertEquals(Color.BLUE, RobotColor.getTrailColor());

        RobotColor.setTrailColor(RobotColor.ColorPreset.GREEN);
        assertEquals(Color.GREEN, RobotColor.getTrailColor());

        RobotColor.setTrailColor(RobotColor.ColorPreset.YELLOW);
        assertEquals(Color.YELLOW, RobotColor.getTrailColor());

        RobotColor.setTrailColor(RobotColor.ColorPreset.PURPLE);
        assertEquals(Color.MAGENTA, RobotColor.getTrailColor());

        RobotColor.setTrailColor(RobotColor.ColorPreset.GRAY);
        assertNotNull(RobotColor.getTrailColor());
        Color trailColor = RobotColor.getTrailColor();
        assertEquals(trailColor.getRed(), trailColor.getGreen());
        assertEquals(trailColor.getGreen(), trailColor.getBlue());
    }

    @Test
    public void testDefaultColors() {
        // Загружаем цвета (должны быть значения по умолчанию)
        RobotColor.loadColors();

        // Проверяем, что цвета установлены (не null)
        assertNotNull(RobotColor.getRobotColor());
        assertNotNull(RobotColor.getTargetColor());
        assertNotNull(RobotColor.getTrailColor());
    }
}