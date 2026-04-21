package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public final class WindowStateManager
{
    private static final String CONFIG_DIRECTORY = ".robots";
    private static final String CONFIG_FILE = "window-state.properties";
    private static final String MAIN_WINDOW_PREFIX = "main";
    private static final String INTERNAL_WINDOW_PREFIX = "internal.";

    private static final int MIN_MAIN_WINDOW_WIDTH = 200;
    private static final int MIN_MAIN_WINDOW_HEIGHT = 200;
    private static final int MIN_INTERNAL_WINDOW_WIDTH = 100;
    private static final int MIN_INTERNAL_WINDOW_HEIGHT = 100;

    private static final String X_SUFFIX = ".x";
    private static final String Y_SUFFIX = ".y";
    private static final String WIDTH_SUFFIX = ".width";
    private static final String HEIGHT_SUFFIX = ".height";
    private static final String EXTENDED_STATE_SUFFIX = ".extendedState";
    private static final String ICON_SUFFIX = ".icon";
    private static final String MAXIMUM_SUFFIX = ".maximum";
    private static final String LAYER_SUFFIX = ".layer";
    private static final String Z_ORDER_SUFFIX = ".zOrder";
    private static final String SELECTED_SUFFIX = ".selected";

    private final Path configPath;

    public WindowStateManager()
    {
        this(getDefaultConfigPath());
    }

    WindowStateManager(Path configPath)
    {
        this.configPath = configPath;
    }

    public void save(JFrame mainFrame, JInternalFrame... internalFrames)
    {
        Properties properties = new Properties();
        storeComponentBounds(properties, MAIN_WINDOW_PREFIX, mainFrame);
        properties.setProperty(MAIN_WINDOW_PREFIX + EXTENDED_STATE_SUFFIX,
            Integer.toString(mainFrame.getExtendedState() & ~Frame.ICONIFIED));

        for (JInternalFrame frame : internalFrames)
        {
            if (frame == null) {
                continue;
            }

            String prefix = getInternalWindowPrefix(frame);
            storeComponentBounds(properties, prefix, frame);
            properties.setProperty(prefix + ICON_SUFFIX, Boolean.toString(frame.isIcon()));
            properties.setProperty(prefix + MAXIMUM_SUFFIX, Boolean.toString(frame.isMaximum()));
            properties.setProperty(prefix + LAYER_SUFFIX, Integer.toString(frame.getLayer()));

            Container parent = frame.getParent();
            if (parent != null) {
                properties.setProperty(prefix + Z_ORDER_SUFFIX,
                    Integer.toString(parent.getComponentZOrder(frame)));
            }
            properties.setProperty(prefix + SELECTED_SUFFIX, Boolean.toString(frame.isSelected()));
        }

        writeProperties(properties);
    }

    public void restore(JFrame mainFrame, JInternalFrame... internalFrames)
    {
        Properties properties = loadProperties();
        if (properties.isEmpty()) {
            return;
        }

        restoreComponentBounds(properties, MAIN_WINDOW_PREFIX, mainFrame,
            MIN_MAIN_WINDOW_WIDTH, MIN_MAIN_WINDOW_HEIGHT);

        Integer extendedState = readInteger(properties, MAIN_WINDOW_PREFIX + EXTENDED_STATE_SUFFIX);
        if (extendedState != null) {
            mainFrame.setExtendedState(extendedState & ~Frame.ICONIFIED);
        }

        JInternalFrame selectedFrame = null;
        for (JInternalFrame frame : internalFrames)
        {
            if (frame == null) {
                continue;
            }

            String prefix = getInternalWindowPrefix(frame);
            restoreComponentBounds(properties, prefix, frame,
                MIN_INTERNAL_WINDOW_WIDTH, MIN_INTERNAL_WINDOW_HEIGHT);
            restoreInternalFrameState(properties, prefix, frame);
            if (Boolean.TRUE.equals(readBoolean(properties, prefix + SELECTED_SUFFIX))) {
                selectedFrame = frame;
            }
        }

        restoreInternalFrameOrder(properties, internalFrames);
        restoreSelectedFrame(selectedFrame);
    }

    public Set<String> getSavedInternalWindowNames()
    {
        Properties properties = loadProperties();
        Set<String> names = new TreeSet<>();
        for (String key : properties.stringPropertyNames()) {
            String windowName = extractInternalWindowName(key);
            if (windowName != null) {
                names.add(windowName);
            }
        }
        return names;
    }

    private void storeComponentBounds(Properties properties, String prefix, Component component)
    {
        Rectangle bounds = component.getBounds();
        properties.setProperty(prefix + X_SUFFIX, Integer.toString(bounds.x));
        properties.setProperty(prefix + Y_SUFFIX, Integer.toString(bounds.y));
        properties.setProperty(prefix + WIDTH_SUFFIX, Integer.toString(bounds.width));
        properties.setProperty(prefix + HEIGHT_SUFFIX, Integer.toString(bounds.height));
    }

    private void restoreComponentBounds(Properties properties, String prefix, Component component,
                                       int minWidth, int minHeight)
    {
        Integer x = readInteger(properties, prefix + X_SUFFIX);
        Integer y = readInteger(properties, prefix + Y_SUFFIX);
        Integer width = readInteger(properties, prefix + WIDTH_SUFFIX);
        Integer height = readInteger(properties, prefix + HEIGHT_SUFFIX);
        if (x == null || y == null || width == null || height == null) {
            return;
        }
        if (width < minWidth || height < minHeight) {
            return;
        }

        component.setBounds(x, y, width, height);
    }

    private void restoreInternalFrameState(Properties properties, String prefix, JInternalFrame frame)
    {
        Integer layer = readInteger(properties, prefix + LAYER_SUFFIX);
        if (layer != null) {
            frame.setLayer(layer);
        }

        Boolean maximized = readBoolean(properties, prefix + MAXIMUM_SUFFIX);
        if (Boolean.TRUE.equals(maximized)) {
            try {
                frame.setMaximum(true);
            } catch (Exception ignored) {
            }
        }

        Boolean iconified = readBoolean(properties, prefix + ICON_SUFFIX);
        if (iconified != null) {
            try {
                frame.setIcon(iconified);
            } catch (Exception ignored) {
            }
        }
    }

    private void restoreInternalFrameOrder(Properties properties, JInternalFrame... internalFrames)
    {
        List<JInternalFrame> orderedFrames = new ArrayList<>();
        for (JInternalFrame frame : internalFrames) {
            if (frame == null || frame.getParent() == null) {
                continue;
            }

            String prefix = getInternalWindowPrefix(frame);
            if (readInteger(properties, prefix + Z_ORDER_SUFFIX) != null) {
                orderedFrames.add(frame);
            }
        }

        orderedFrames.sort(Comparator.<JInternalFrame>comparingInt(
            frame -> readInteger(properties, getInternalWindowPrefix(frame) + Z_ORDER_SUFFIX)
        ).reversed());

        for (JInternalFrame frame : orderedFrames) {
            Container parent = frame.getParent();
            Integer zOrder = readInteger(properties, getInternalWindowPrefix(frame) + Z_ORDER_SUFFIX);
            if (parent == null || zOrder == null) {
                continue;
            }

            int restoredOrder = Math.max(0, Math.min(zOrder, parent.getComponentCount() - 1));
            parent.setComponentZOrder(frame, restoredOrder);
        }
    }

    private void restoreSelectedFrame(JInternalFrame selectedFrame)
    {
        if (selectedFrame == null) {
            return;
        }

        try {
            selectedFrame.setSelected(true);
            selectedFrame.toFront();
        } catch (Exception ignored) {
        }
    }

    private void writeProperties(Properties properties)
    {
        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "Robots window state");
            }
        } catch (IOException ignored) {
        }
    }

    private Properties loadProperties()
    {
        Properties properties = new Properties();
        if (!Files.exists(configPath)) {
            return properties;
        }

        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        } catch (IOException ignored) {
        }
        return properties;
    }

    private String getInternalWindowPrefix(JInternalFrame frame)
    {
        String name = frame.getName();
        if (name == null || name.isBlank()) {
            name = frame.getClass().getSimpleName();
        }
        return INTERNAL_WINDOW_PREFIX + name;
    }

    private String extractInternalWindowName(String key)
    {
        if (!key.startsWith(INTERNAL_WINDOW_PREFIX)) {
            return null;
        }

        int nameStart = INTERNAL_WINDOW_PREFIX.length();
        int nameEnd = key.indexOf('.', nameStart);
        if (nameEnd < 0) {
            return null;
        }
        return key.substring(nameStart, nameEnd);
    }

    private Integer readInteger(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Boolean readBoolean(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    private static Path getDefaultConfigPath()
    {
        return Path.of(System.getProperty("user.home"), CONFIG_DIRECTORY, CONFIG_FILE);
    }
}
