package gui;

import factory.MenuBarFactory;
import factory.WindowFactory;
import config.WindowState;
import config.ColorSettings;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения координирует работу.
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private GameWindow gameWindow;
    private LogWindow logWindow;

    private static final int WINDOW_INSET = 50;
    private static final int YES_BUTTON_INDEX = 0;
    private static final int NO_BUTTON_INDEX = 1;

    public MainApplicationFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(WINDOW_INSET, WINDOW_INSET,
                screenSize.width - WINDOW_INSET * 2,
                screenSize.height - WINDOW_INSET * 2);

        setContentPane(desktopPane);
        createWindows();

        // Регистрируем окна для сохранения состояния
        WindowState.getInstance().registerWindows(this, gameWindow, logWindow);

        WindowState.getInstance().load();
        ColorSettings.getInstance().load();

        setupMenu();
        setupWindowClosing();
    }

    /**
     * Создает внутренние окна приложения
     */
    private void createWindows() {
        gameWindow = WindowFactory.createGameWindow();
        addWindow(gameWindow);

        logWindow = WindowFactory.createLogWindow();
        addWindow(logWindow);
        CoordinateWindow coordinateWindow = WindowFactory.createCoordinateWindow(gameWindow.getVisualizer().getRobot());
        addWindow(coordinateWindow);
        gameWindow.getVisualizer().getMovementService().addListener(coordinateWindow);
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

        // Если это игровое окно — обновляем границы робота
        if (frame instanceof GameWindow) {
            GameWindow game_wind = (GameWindow) frame;
            GameVisualizer visualizer = game_wind.getVisualizer();
            if (visualizer != null) {
                SwingUtilities.invokeLater(visualizer::updateRobotBounds);
            }
        }
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
            // Сохраняем состояние окон и цветов
            WindowState.getInstance().save();
            ColorSettings.getInstance().save();

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