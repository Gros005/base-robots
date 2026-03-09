package factory;

import gui.GameWindow;
import gui.LogWindow;
import gui.GameVisualizer;
import log.Logger;
import model.Robot;
import service.RobotMovementService;

/**
 * Создание и настройка окон приложения.
 */
public class WindowFactory {

    private static int windowCounter = 1;

    /**
     * Создает первое окно
     */
    public static GameWindow createGameWindow() {
        return createGameWindow(100, 100, 150, 100);
    }

    /**
     * Создает новое окно с роботом в стартовой позиции
     */
    public static GameWindow createNewGameWindow() {
        // Смещаем каждое новое окно на 30 пикселей вправо и вниз
        int offset = windowCounter * 30;
        windowCounter++;

        GameWindow window = createGameWindow(100 + offset, 100 + offset, 150 + offset, 100 + offset);

        window.setTitle("Игровое поле " + windowCounter);
        window.setLocation(offset, offset);

        return window;
    }

    /**
     * Метод для создания окна с заданными параметрами
     */
    private static GameWindow createGameWindow(int robotX, int robotY, int targetX, int targetY) {

        Robot robot = new Robot(robotX, robotY, targetX, targetY);

        RobotMovementService movementService = new RobotMovementService(robot);

        GameVisualizer visualizer = new GameVisualizer(robot, movementService);

        GameWindow gameWindow = new GameWindow(visualizer);
        gameWindow.setSize(400, 400);

        return gameWindow;
    }

    /**
     * Создает окно с логами
     */
    public static LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        logWindow.pack();

        Logger.debug("Протокол работает");

        return logWindow;
    }

    /**
     * Сбросить счетчик окон
     */
    public static void resetCounter() {
        windowCounter = 1;
    }
}