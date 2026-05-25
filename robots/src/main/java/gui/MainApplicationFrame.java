package gui;

import factory.MenuBarFactory;
import factory.WindowFactory;
import config.WindowState;
import config.ColorSettings;

import javax.swing.*;
import java.awt.*;

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

        WindowState.getInstance().registerWindows(this, gameWindow, logWindow);
        WindowState.getInstance().load();
        ColorSettings.getInstance().load();

        setupMenu();
        setupWindowClosing();
    }

    private void createWindows() {
        gameWindow = WindowFactory.createGameWindow();
        addWindow(gameWindow);

        logWindow = WindowFactory.createLogWindow();
        addWindow(logWindow);

        CoordinateWindow coordinateWindow = WindowFactory.createCoordinateWindow();
        addWindow(coordinateWindow);

        gameWindow.getSinglePanel().setCoordinateWindow(coordinateWindow);
    }

    private void setupMenu() {
        MenuBarFactory menuBarFactory = new MenuBarFactory(this, gameWindow);
        setJMenuBar(menuBarFactory.createMenuBar());
    }

    private void setupWindowClosing() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }

    public void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

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
            WindowState.getInstance().save();
            ColorSettings.getInstance().save();

            if (gameWindow != null) {
                gameWindow.shutdown();
            }

            log.Logger.debug(Language.get("log.appClosing"));
            System.exit(0);
        } else {
            log.Logger.debug(Language.get("log.exitCancelled"));
        }
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public void startRaceSetup() {
        int robotCount = RaceSetupDialog.showRobotCountDialog(this);
        RaceSetupDialog.PlacementMode mode = RaceSetupDialog.showPlacementModeDialog(this);
        gameWindow.switchToRaceMode(robotCount, mode);
    }
}