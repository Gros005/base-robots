package gui;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.swing.*;

public class RobotsProgram
{
    public static void main(String[] args) {
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

      } catch (Exception e) {
        e.printStackTrace();
      }
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame();
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      });
    }

    public static final class WindowStateManager {
        private static final String DIR_NAME = ".robots";
        private static final String FILE_NAME = "window-state.properties";

        private WindowStateManager() {}

        public static Path getConfigPath() {
            String home = System.getProperty("user.home");
            return Path.of(home, DIR_NAME, FILE_NAME);
        }

        public static void save(JFrame mainFrame, JInternalFrame... internalFrames) {
            Properties p = new Properties();

            Rectangle b = mainFrame.getBounds();
            p.setProperty("main.x", Integer.toString(b.x));
            p.setProperty("main.y", Integer.toString(b.y));
            p.setProperty("main.w", Integer.toString(b.width));
            p.setProperty("main.h", Integer.toString(b.height));
            p.setProperty("main.extendedState", Integer.toString(mainFrame.getExtendedState()));

            for (JInternalFrame f : internalFrames) {
                if (f == null) continue;
                String key = internalKey(f);

                Rectangle ib = f.getBounds();
                p.setProperty(key + ".x", Integer.toString(ib.x));
                p.setProperty(key + ".y", Integer.toString(ib.y));
                p.setProperty(key + ".w", Integer.toString(ib.width));
                p.setProperty(key + ".h", Integer.toString(ib.height));

                try {
                    p.setProperty(key + ".icon", Boolean.toString(f.isIcon()));
                    p.setProperty(key + ".max", Boolean.toString(f.isMaximum()));
                } catch (Exception ignored) {
                }
            }

            Path cfg = getConfigPath();
            try {
                Files.createDirectories(cfg.getParent());
                try (OutputStream os = Files.newOutputStream(cfg)) {
                    p.store(os, "Robots window state");
                }
            } catch (IOException ignored) {
            }
        }

        public static void restore(JFrame mainFrame, JInternalFrame... internalFrames) {
            Path cfg = getConfigPath();
            if (!Files.exists(cfg)) return;

            Properties p = new Properties();
            try (InputStream is = Files.newInputStream(cfg)) {
                p.load(is);
            } catch (IOException e) {
                return;
            }

            Integer x = intOrNull(p.getProperty("main.x"));
            Integer y = intOrNull(p.getProperty("main.y"));
            Integer w = intOrNull(p.getProperty("main.w"));
            Integer h = intOrNull(p.getProperty("main.h"));

            if (x != null && y != null && w != null && h != null && w > 50 && h > 50) {
                mainFrame.setBounds(x, y, w, h);
            }

            Integer state = intOrNull(p.getProperty("main.extendedState"));
            if (state != null) {
                mainFrame.setExtendedState(state);
            }

            for (JInternalFrame f : internalFrames) {
                if (f == null) continue;
                String key = internalKey(f);

                Integer ix = intOrNull(p.getProperty(key + ".x"));
                Integer iy = intOrNull(p.getProperty(key + ".y"));
                Integer iw = intOrNull(p.getProperty(key + ".w"));
                Integer ih = intOrNull(p.getProperty(key + ".h"));
                if (ix != null && iy != null && iw != null && ih != null && iw > 20 && ih > 20) {
                    f.setBounds(ix, iy, iw, ih);
                }

                Boolean icon = boolOrNull(p.getProperty(key + ".icon"));
                Boolean max = boolOrNull(p.getProperty(key + ".max"));

                try {
                    if (max != null && max) f.setMaximum(true);
                } catch (Exception ignored) {}
                try {
                    if (icon != null) f.setIcon(icon);
                } catch (Exception ignored) {}
            }
        }

        private static String internalKey(JInternalFrame f) {
            String n = f.getName();
            if (n != null && !n.isBlank()) return "if." + n;
            return "if." + f.getClass().getSimpleName();
        }

        private static Integer intOrNull(String s) {
            if (s == null) return null;
            try { return Integer.parseInt(s.trim()); }
            catch (Exception e) { return null; }
        }

        private static Boolean boolOrNull(String s) {
            if (s == null) return null;
            return Boolean.parseBoolean(s.trim());
        }
    }
}
