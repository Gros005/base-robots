package plugin;

import java.io.IOException;
import java.nio.file.Path;

public final class LoadedRobotPlugin implements AutoCloseable {
    private final Path sourcePath;
    private final RobotPlugin plugin;
    private final AutoCloseable resource;

    public LoadedRobotPlugin(Path sourcePath, RobotPlugin plugin, AutoCloseable resource) {
        this.sourcePath = sourcePath;
        this.plugin = plugin;
        this.resource = resource;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public RobotPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void close() throws IOException {
        try {
            resource.close();
        } catch (IOException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IOException("Не удалось закрыть ресурсы плагина", exception);
        }
    }
}
