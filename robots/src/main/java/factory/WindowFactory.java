package factory;

import gui.GameVisualizer;
import gui.GameWindow;
import gui.LogWindow;
import log.Logger;
import model.Robot;
import plugin.DefaultRobotPlugin;
import plugin.LoadedRobotPlugin;
import plugin.RobotPlugin;
import plugin.RobotPluginLoader;
import service.RobotMovementService;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Создание и настройка окон приложения.
 */
public class WindowFactory {
    private static final int DEFAULT_ROBOT_X = 100;
    private static final int DEFAULT_ROBOT_Y = 100;
    private static final int DEFAULT_TARGET_X = 150;
    private static final int DEFAULT_TARGET_Y = 100;
    private static final int WINDOW_OFFSET = 30;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int LOG_WINDOW_X = 10;
    private static final int LOG_WINDOW_Y = 10;
    private static final int LOG_WINDOW_WIDTH = 300;
    private static final int LOG_WINDOW_HEIGHT = 800;
    private static final String DEFAULT_GAME_WINDOW_TITLE = "Игровое поле";
    private static final String PLUGIN_WINDOW_TITLE_PREFIX = "Загруженный робот: ";

    private static final RobotPlugin DEFAULT_PLUGIN = new DefaultRobotPlugin();
    private static int windowCounter = 1;

    public static GameWindow createGameWindow() {
        return createGameWindow(
            DEFAULT_PLUGIN,
            () -> {
            },
            DEFAULT_GAME_WINDOW_TITLE,
            DEFAULT_ROBOT_X,
            DEFAULT_ROBOT_Y,
            DEFAULT_TARGET_X,
            DEFAULT_TARGET_Y
        );
    }

    public static GameWindow createNewGameWindow() {
        int offset = windowCounter * WINDOW_OFFSET;

        GameWindow window = createGameWindow(
            DEFAULT_PLUGIN,
            () -> {
            },
            DEFAULT_GAME_WINDOW_TITLE + " " + windowCounter,
            DEFAULT_ROBOT_X + offset,
            DEFAULT_ROBOT_Y + offset,
            DEFAULT_TARGET_X + offset,
            DEFAULT_TARGET_Y + offset
        );
        window.setLocation(offset, offset);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        windowCounter++;
        return window;
    }

    public static GameWindow createJarRobotWindow(Path jarPath) throws IOException {
        LoadedRobotPlugin loadedPlugin = new RobotPluginLoader().load(jarPath);
        int offset = windowCounter * WINDOW_OFFSET;

        GameWindow window = createGameWindow(
            loadedPlugin.getPlugin(),
            () -> closeLoadedPlugin(loadedPlugin),
            PLUGIN_WINDOW_TITLE_PREFIX + loadedPlugin.getPlugin().getDisplayName(),
            DEFAULT_ROBOT_X + offset,
            DEFAULT_ROBOT_Y + offset,
            DEFAULT_TARGET_X + offset,
            DEFAULT_TARGET_Y + offset
        );
        window.setLocation(offset, offset);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        windowCounter++;
        return window;
    }

    public static LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(LOG_WINDOW_X, LOG_WINDOW_Y);
        logWindow.setSize(LOG_WINDOW_WIDTH, LOG_WINDOW_HEIGHT);
        logWindow.pack();

        Logger.debug("Протокол работает");
        return logWindow;
    }

    public static void resetCounter() {
        windowCounter = 1;
    }

    private static GameWindow createGameWindow(
        RobotPlugin robotPlugin,
        Runnable closeAction,
        String title,
        int robotX,
        int robotY,
        int targetX,
        int targetY
    ) {
        Robot robot = new Robot(robotX, robotY, targetX, targetY);
        RobotMovementService movementService = new RobotMovementService(robot, robotPlugin.createController());
        GameVisualizer visualizer = new GameVisualizer(robot, movementService, robotPlugin.createRenderer());

        GameWindow gameWindow = new GameWindow(visualizer, closeAction);
        gameWindow.setTitle(title);
        gameWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        return gameWindow;
    }

    private static void closeLoadedPlugin(LoadedRobotPlugin loadedPlugin) {
        try {
            loadedPlugin.close();
        } catch (IOException ignored) {
        }
    }
}
