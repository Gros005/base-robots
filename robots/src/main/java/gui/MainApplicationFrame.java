package gui;

import factory.MenuBarFactory;
import factory.WindowFactory;
import log.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Главное окно приложения координирует работу.
 */
public class MainApplicationFrame extends JFrame {
    private static final int WINDOW_INSET = 50;
    private static final int YES_BUTTON_INDEX = 0;
    private static final int NO_BUTTON_INDEX = 1;

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager = new WindowStateManager();

    private GameWindow gameWindow;
    private LogWindow logWindow;

    public MainApplicationFrame() {
        initializeMainWindowBounds();
        setContentPane(desktopPane);
        createWindows();
        restoreWindowState();
        setupMenu();
        setupWindowClosing();
    }

    private void initializeMainWindowBounds() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(
            WINDOW_INSET,
            WINDOW_INSET,
            screenSize.width - WINDOW_INSET * 2,
            screenSize.height - WINDOW_INSET * 2
        );
    }

    private void createWindows() {
        gameWindow = WindowFactory.createGameWindow();
        gameWindow.setName("gameWindow");
        addWindow(gameWindow);

        logWindow = WindowFactory.createLogWindow();
        logWindow.setName("logWindow");
        addWindow(logWindow);
    }

    private void restoreWindowState() {
        windowStateManager.restore(this, desktopPane.getAllFrames());
    }

    private void setupMenu() {
        MenuBarFactory menuBarFactory = new MenuBarFactory(this, gameWindow);
        setJMenuBar(menuBarFactory.createMenuBar());
    }

    private void setupWindowClosing() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
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
            windowStateManager.save(this, desktopPane.getAllFrames());

            if (gameWindow != null && gameWindow.getVisualizer() != null) {
                gameWindow.getVisualizer().shutdown();
            }

            Logger.debug(Language.get("log.appClosing"));
            System.exit(0);
            return;
        }

        Logger.debug(Language.get("log.exitCancelled"));
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
}
