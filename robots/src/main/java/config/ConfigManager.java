package config;

import java.io.*;
import java.util.Properties;

/**
 * Управляет сохранением и загрузкой настроек приложения
 */
public class ConfigManager {

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.robots";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";

    public static Properties loadProperties() {
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

    public static boolean saveProperties(Properties props) {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.err.println("[Config] Failed to create directory: " + CONFIG_DIR);
                    return false;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "Robot application settings");
                System.out.println("[Config] Saved to: " + CONFIG_FILE);
                return true;
            }
        } catch (IOException e) {
            System.err.println("[Config] Failed to save: " + e.getMessage());
            return false;
        }
    }

    public static String getConfigPath() {
        return CONFIG_FILE;
    }

    public static String getConfigDir() {
        return CONFIG_DIR;
    }
}