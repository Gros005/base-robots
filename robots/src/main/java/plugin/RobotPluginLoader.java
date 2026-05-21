package plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class RobotPluginLoader {
    public LoadedRobotPlugin load(Path jarPath) throws IOException {
        validateJarPath(jarPath);
        ensureJarArchive(jarPath);

        URLClassLoader classLoader = new URLClassLoader(
            new URL[]{jarPath.toUri().toURL()},
            RobotPlugin.class.getClassLoader()
        );

        try {
            ServiceLoader<RobotPlugin> serviceLoader = ServiceLoader.load(RobotPlugin.class, classLoader);
            Iterator<RobotPlugin> plugins = serviceLoader.iterator();
            if (!plugins.hasNext()) {
                throw new IOException("В jar не найдена реализация plugin.RobotPlugin");
            }

            RobotPlugin plugin = plugins.next();
            if (plugin.createController() == null) {
                throw new IOException("Плагин не создал контроллер робота");
            }
            if (plugin.createRenderer() == null) {
                throw new IOException("Плагин не создал визуализатор робота");
            }

            return new LoadedRobotPlugin(jarPath, plugin, classLoader);
        } catch (Exception exception) {
            try {
                classLoader.close();
            } catch (IOException ignored) {
            }
            if (exception instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Не удалось загрузить плагин робота", exception);
        }
    }

    private void validateJarPath(Path jarPath) throws IOException {
        if (jarPath == null) {
            throw new IOException("Файл плагина не выбран");
        }
        if (!Files.exists(jarPath) || !Files.isRegularFile(jarPath)) {
            throw new IOException("Файл плагина не найден");
        }
        if (!jarPath.getFileName().toString().toLowerCase().endsWith(".jar")) {
            throw new IOException("Нужно выбрать jar-архив");
        }
    }

    private void ensureJarArchive(Path jarPath) throws IOException {
        try (JarFile ignored = new JarFile(jarPath.toFile())) {
            // validation only
        } catch (ZipException exception) {
            throw new IOException("Выбранный файл не является корректным jar-архивом", exception);
        }
    }
}
