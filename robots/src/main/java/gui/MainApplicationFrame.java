package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private static final int FRAME_INSET = 50;
    private static final int LOG_WINDOW_X = 10;
    private static final int LOG_WINDOW_Y = 10;
    private static final int LOG_WINDOW_WIDTH = 300;
    private static final int LOG_WINDOW_HEIGHT = 800;
    private static final int GAME_WINDOW_WIDTH = 400;
    private static final int GAME_WINDOW_HEIGHT = 400;
    private static final String LOG_WINDOW_NAME = "logWindow";
    private static final String GAME_WINDOW_NAME = "gameWindow";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager = new WindowStateManager();
    private final LogWindow logWindow;
    private final GameWindow gameWindow;

    public MainApplicationFrame()
    {
        initializeMainFrameBounds();
        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = createGameWindow();
        addWindow(gameWindow);

        windowStateManager.restore(this, logWindow, gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event)
            {
                windowStateManager.save(MainApplicationFrame.this, logWindow, gameWindow);
            }
        });
    }

    private void initializeMainFrameBounds()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(FRAME_INSET, FRAME_INSET,
            screenSize.width - FRAME_INSET * 2,
            screenSize.height - FRAME_INSET * 2);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow window = new LogWindow(Logger.getDefaultLogSource());
        window.setName(LOG_WINDOW_NAME);
        window.setLocation(LOG_WINDOW_X, LOG_WINDOW_Y);
        window.setSize(LOG_WINDOW_WIDTH, LOG_WINDOW_HEIGHT);
        setMinimumSize(window.getSize());
        window.pack();
        Logger.debug("Протокол работает");
        return window;
    }

    protected GameWindow createGameWindow()
    {
        GameWindow window = new GameWindow();
        window.setName(GAME_WINDOW_NAME);
        window.setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        return window;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
            "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException exception)
        {
            Logger.error("Не удалось изменить схему отображения: " + exception.getMessage());
        }
    }
}
