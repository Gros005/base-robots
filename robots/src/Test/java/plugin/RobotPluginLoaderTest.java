package plugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotPluginLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    public void testRejectsNonJarFiles() throws Exception {
        Path file = Files.writeString(tempDir.resolve("not-a-plugin.txt"), "test", StandardCharsets.UTF_8);

        IOException exception = assertThrows(IOException.class, () -> new RobotPluginLoader().load(file));
        assertTrue(exception.getMessage().contains("jar"));
    }

    @Test
    public void testLoadsPluginFromJar() throws Exception {
        Path jarPath = createPluginJar();

        try (LoadedRobotPlugin loadedPlugin = new RobotPluginLoader().load(jarPath)) {
            assertEquals("Тестовый робот", loadedPlugin.getPlugin().getDisplayName());
            assertNotNull(loadedPlugin.getPlugin().createController());
            assertNotNull(loadedPlugin.getPlugin().createRenderer());
        }
    }

    private Path createPluginJar() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("JavaCompiler недоступен");
        }

        Path sourceRoot = tempDir.resolve("src");
        Path sourceFile = sourceRoot.resolve("testplugin/TestRobotPlugin.java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, getPluginSource(), StandardCharsets.UTF_8);

        Path classesDir = tempDir.resolve("classes");
        Files.createDirectories(classesDir);

        int compileResult = compiler.run(
            null,
            null,
            null,
            "-classpath",
            System.getProperty("java.class.path"),
            "-d",
            classesDir.toString(),
            sourceFile.toString()
        );
        assertEquals(0, compileResult);

        Path servicesFile = classesDir.resolve("META-INF/services/plugin.RobotPlugin");
        Files.createDirectories(servicesFile.getParent());
        Files.writeString(servicesFile, "testplugin.TestRobotPlugin", StandardCharsets.UTF_8);

        Path jarPath = tempDir.resolve("test-plugin.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jarPath))) {
            Files.walk(classesDir)
                .filter(Files::isRegularFile)
                .forEach(path -> writeJarEntry(classesDir, path, output));
        }
        return jarPath;
    }

    private void writeJarEntry(Path root, Path file, JarOutputStream output) {
        Path relativePath = root.relativize(file);
        String entryName = relativePath.toString().replace('\\', '/');
        try {
            output.putNextEntry(new JarEntry(entryName));
            output.write(Files.readAllBytes(file));
            output.closeEntry();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private String getPluginSource() {
        return """
            package testplugin;

            import model.Robot;
            import plugin.RobotController;
            import plugin.RobotPlugin;
            import plugin.RobotRenderer;

            import java.awt.Color;
            import java.awt.Graphics2D;

            public class TestRobotPlugin implements RobotPlugin {
                @Override
                public String getDisplayName() {
                    return "Тестовый робот";
                }

                @Override
                public RobotController createController() {
                    return new StraightController();
                }

                @Override
                public RobotRenderer createRenderer() {
                    return new SquareRenderer();
                }

                public static class StraightController implements RobotController {
                    @Override
                    public void update(Robot robot, double durationMs) {
                        robot.move(robot.getMaxVelocity(), 0, durationMs);
                    }
                }

                public static class SquareRenderer implements RobotRenderer {
                    @Override
                    public void paint(Graphics2D graphics, Robot robot) {
                        graphics.setColor(Color.BLUE);
                        graphics.fillRect((int) robot.getPositionX(), (int) robot.getPositionY(), 10, 10);
                    }
                }
            }
            """;
    }
}
