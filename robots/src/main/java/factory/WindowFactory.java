package factory;

import gui.GameWindow;
import gui.LogWindow;
import gui.CoordinateWindow;
import log.Logger;

public class WindowFactory {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int LOG_WINDOW_X = 10;
    private static final int LOG_WINDOW_Y = 10;
    private static final int LOG_WINDOW_WIDTH = 300;
    private static final int LOG_WINDOW_HEIGHT = 800;

    public static GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameWindow.switchToSingleMode();
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

    public static CoordinateWindow createCoordinateWindow() {
        CoordinateWindow window = new CoordinateWindow();
        window.setSize(250, 150);
        window.setLocation(400, 500);
        return window;
    }
}