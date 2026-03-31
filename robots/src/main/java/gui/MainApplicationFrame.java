package gui;

import config.RobotColor;
import factory.MenuBarFactory;
import factory.WindowFactory;
import config.WindowState;
import service.RobotMovementService;
import model.Robot;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения координирует работу.
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private GameWindow gameWindow;

    private static final int WINDOW_INSET = 50;
    private static final int YES_BUTTON_INDEX = 0;
    private static final int NO_BUTTON_INDEX = 1;

    public MainApplicationFrame() {
        // Размеры окна
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(WINDOW_INSET, WINDOW_INSET,
                screenSize.width - WINDOW_INSET * 2,
                screenSize.height - WINDOW_INSET * 2);

        setContentPane(desktopPane);
        createWindows();
        restoreWindowsState();
        RobotColor.loadColors();
        setupMenu();
        setupWindowClosing();
    }

    /**
     * Восстанавливает сохраненное состояние окон
     */
    private void restoreWindowsState() {
        WindowState.restoreWindowState("main", this);

        if (gameWindow != null) {
            WindowState.restoreWindowState("gameWindow", gameWindow);
        }

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof LogWindow) {
                WindowState.restoreWindowState("logWindow", frame);
            }
        }
    }

    /**
     * Сохраняет состояние всех окон
     */
    private void saveWindowsState() {
        WindowState.saveWindowState("main", this);

        if (gameWindow != null) {
            WindowState.saveWindowState("gameWindow", gameWindow);
        }

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof LogWindow) {
                WindowState.saveWindowState("logWindow", frame);
            }
        }
    }

    /**
     * Создает внутренние окна приложения
     */
    private void createWindows() {
        gameWindow = WindowFactory.createGameWindow();
        addWindow(gameWindow);

        LogWindow logWindow = WindowFactory.createLogWindow();
        addWindow(logWindow);

        model.Robot robot = gameWindow.getVisualizer().getRobot();
        CoordinateWindow coordinateWindow = WindowFactory.createCoordinateWindow(robot);
        addWindow(coordinateWindow);

        RobotMovementService movementService = gameWindow.getVisualizer().getMovementService();
        movementService.addListener(coordinateWindow);
    }

    /**
     * Настраивает меню приложения
     */
    private void setupMenu() {
        MenuBarFactory menuBarFactory = new MenuBarFactory(this, gameWindow);
        setJMenuBar(menuBarFactory.createMenuBar());
    }

    /**
     * Настраивает обработку закрытия окна
     */
    private void setupWindowClosing() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }

    /**
     * Добавляет внутреннее окно
     */
    public void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Обработка выхода из приложения
     */
    private void exitApplication() {
        String[] buttons = {
                Language.get("dialog.exit.yes"),
                Language.get("dialog.exit.no")
        };

        int result = JOptionPane.showOptionDialog(
                this,
                Language.get("dialog.exit.message"),
                Language.get("dialog.exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                buttons,
                buttons[NO_BUTTON_INDEX]
        );

        if (result == YES_BUTTON_INDEX) {
            saveWindowsState();

            if (gameWindow != null && gameWindow.getVisualizer() != null) {
                gameWindow.getVisualizer().shutdown();
            }

            log.Logger.debug(Language.get("log.appClosing"));
            System.exit(0);
        } else {
            log.Logger.debug(Language.get("log.exitCancelled"));
        }
    }

    /**
     * Для доступа из MenuBarFactory
     */
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
}