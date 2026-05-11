package config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

public class ColorSettingsTest {

    private ColorSettings colorSettings;

    @BeforeEach
    public void setUp() {
        colorSettings = ColorSettings.getInstance();
    }

    @Test
    public void testRobotColorPresets() {
        assertEquals(Color.RED, ColorSettings.ColorPreset.RED.color);
        assertEquals(Color.BLUE, ColorSettings.ColorPreset.BLUE.color);
        assertEquals(Color.GREEN, ColorSettings.ColorPreset.GREEN.color);
        assertEquals(Color.YELLOW, ColorSettings.ColorPreset.YELLOW.color);
        assertEquals(Color.MAGENTA, ColorSettings.ColorPreset.PURPLE.color);
        assertEquals(Color.GRAY, ColorSettings.ColorPreset.GRAY.color);
    }

    @Test
    public void testRobotColorPresetKeys() {
        assertEquals("red", ColorSettings.ColorPreset.RED.key);
        assertEquals("blue", ColorSettings.ColorPreset.BLUE.key);
        assertEquals("green", ColorSettings.ColorPreset.GREEN.key);
        assertEquals("yellow", ColorSettings.ColorPreset.YELLOW.key);
        assertEquals("purple", ColorSettings.ColorPreset.PURPLE.key);
        assertEquals("gray", ColorSettings.ColorPreset.GRAY.key);
    }

    @Test
    public void testSetAndGetRobotColor() {
        colorSettings.setRobotColor(ColorSettings.ColorPreset.RED);
        assertEquals(Color.RED, colorSettings.getRobotColor());

        colorSettings.setRobotColor(ColorSettings.ColorPreset.BLUE);
        assertEquals(Color.BLUE, colorSettings.getRobotColor());
    }

    @Test
    public void testSetAndGetTargetColor() {
        colorSettings.setTargetColor(ColorSettings.ColorPreset.GREEN);
        assertEquals(Color.GREEN, colorSettings.getTargetColor());

        colorSettings.setTargetColor(ColorSettings.ColorPreset.YELLOW);
        assertEquals(Color.YELLOW, colorSettings.getTargetColor());
    }

    @Test
    public void testSetAndGetTrailColor() {
        colorSettings.setTrailColor(ColorSettings.ColorPreset.RED);
        assertEquals(Color.RED, colorSettings.getTrailColor());

        colorSettings.setTrailColor(ColorSettings.ColorPreset.BLUE);
        assertEquals(Color.BLUE, colorSettings.getTrailColor());

        colorSettings.setTrailColor(ColorSettings.ColorPreset.GREEN);
        assertEquals(Color.GREEN, colorSettings.getTrailColor());

        colorSettings.setTrailColor(ColorSettings.ColorPreset.YELLOW);
        assertEquals(Color.YELLOW, colorSettings.getTrailColor());

        colorSettings.setTrailColor(ColorSettings.ColorPreset.PURPLE);
        assertEquals(Color.MAGENTA, colorSettings.getTrailColor());

        colorSettings.setTrailColor(ColorSettings.ColorPreset.GRAY);
        assertNotNull(colorSettings.getTrailColor());

        Color trailColor = colorSettings.getTrailColor();
        assertEquals(trailColor.getRed(), trailColor.getGreen());
        assertEquals(trailColor.getGreen(), trailColor.getBlue());
    }

    @Test
    public void testSaveAndLoad() {
        colorSettings.setRobotColor(ColorSettings.ColorPreset.RED);

        boolean saved = colorSettings.save();
        assertTrue(saved);

        colorSettings.load();

        assertEquals(Color.RED, colorSettings.getRobotColor());
    }
}