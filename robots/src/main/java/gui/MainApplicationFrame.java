package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        setJMenuBar(createMenuBar());

        // Настройка закрытия окна
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    protected JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(Language.get("menu.file"));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        // Пункт New
        JMenuItem newItem = new JMenuItem(Language.get("menu.file.new"));
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        newItem.addActionListener((event) -> {
            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                if (frame instanceof GameWindow) {
                    GameWindow gameWindow = (GameWindow) frame;
                    gameWindow.getVisualizer().resetRobot();
                    break;
                }
            }

            log.Logger.debug(Language.get("log.robotReset"));
            JOptionPane.showMessageDialog(
                    this,
                    Language.get("dialog.new.message"),
                    Language.get("dialog.new.title"),
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        fileMenu.add(newItem);

        fileMenu.addSeparator();

        // Пункт Quit
        JMenuItem quitItem = new JMenuItem(Language.get("menu.file.quit"));
        quitItem.setMnemonic(KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        quitItem.addActionListener((event) -> {
            exitApplication();
        });
        fileMenu.add(quitItem);

        // МЕНЮ ВЫБОРА ЯЗЫКА
        JMenu languageMenu = new JMenu(Language.get("menu.language"));
        languageMenu.setMnemonic(KeyEvent.VK_L);

        // Русский язык
        JMenuItem russianItem = new JMenuItem(Language.get("menu.language.russian"));
        russianItem.addActionListener((event) -> {
            Language.setLanguage(Language.Lang.RUSSIAN);
            updateUILanguage(); // Метод для обновления интерфейса
        });
        languageMenu.add(russianItem);

        // Английский язык
        JMenuItem englishItem = new JMenuItem(Language.get("menu.language.english"));
        englishItem.addActionListener((event) -> {
            Language.setLanguage(Language.Lang.ENGLISH);
            updateUILanguage(); // Метод для обновления интерфейса
        });
        languageMenu.add(englishItem);

        JMenu lookAndFeelMenu = new JMenu(Language.get("menu.view.lookAndFeel"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem(Language.get("menu.view.lookAndFeel.system"), KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem(Language.get("menu.view.lookAndFeel.cross"), KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu(Language.get("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem(Language.get("menu.tests.addLog"), KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                log.Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        menuBar.add(fileMenu);
        menuBar.add(languageMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private void updateUILanguage() {
        setJMenuBar(createMenuBar());
        setTitle(Language.get("app.title"));

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof GameWindow) {
                frame.setTitle("Game Window");
            } else if (frame instanceof LogWindow) {
                frame.setTitle("Log Window");
            }
        }
        revalidate();
        repaint();
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private boolean exitApplication() {
        String title = Language.get("dialog.exit.title");
        String message = Language.get("dialog.exit.message");
        String yesButton = Language.get("dialog.exit.yes");
        String noButton = Language.get("dialog.exit.no");

        String[] buttons = {yesButton, noButton};

        int result = JOptionPane.showOptionDialog(
                this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                buttons,
                buttons[1]
        );

        if (result == 0) {
            log.Logger.debug(Language.get("log.appClosing"));
            System.exit(0);
            return true;
        } else {
            log.Logger.debug(Language.get("log.exitCancelled"));
            return false;
        }
    }

}