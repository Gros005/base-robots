package factory;

import gui.GameVisualizer;
import gui.GameWindow;
import gui.LogWindow;
import log.Logger;
import model.Robot;
import service.RobotMovementService;

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

    private static int windowCounter = 1;

    public static GameWindow createGameWindow() {
        return createGameWindow(
            DEFAULT_ROBOT_X,
            DEFAULT_ROBOT_Y,
            DEFAULT_TARGET_X,
            DEFAULT_TARGET_Y
        );
    }

    public static GameWindow createNewGameWindow() {
        int offset = windowCounter * WINDOW_OFFSET;

        GameWindow window = createGameWindow(
            DEFAULT_ROBOT_X + offset,
            DEFAULT_ROBOT_Y + offset,
            DEFAULT_TARGET_X + offset,
            DEFAULT_TARGET_Y + offset
        );
        window.setTitle("Игровое поле " + windowCounter);
        window.setLocation(offset, offset);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        windowCounter++;

        return window;
    }

    private static GameWindow createGameWindow(int robotX, int robotY, int targetX, int targetY) {
        Robot robot = new Robot(robotX, robotY, targetX, targetY);
        RobotMovementService movementService = new RobotMovementService(robot);
        GameVisualizer visualizer = new GameVisualizer(robot, movementService);

        GameWindow gameWindow = new GameWindow(visualizer);
        gameWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        return gameWindow;
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
}
