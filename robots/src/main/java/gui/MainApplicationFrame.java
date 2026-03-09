package gui;

import factory.MenuBarFactory;
import factory.WindowFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно приложения координирует работу.
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        // Размеры окна
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        createWindows();
        setupMenu();
        setupWindowClosing();
    }

    /**
     * Создает внутренние окна приложения
     */
    private void createWindows() {
        // Создаем игровое окно
        gameWindow = WindowFactory.createGameWindow();
        addWindow(gameWindow);

        // Создаем окно логов
        LogWindow logWindow = WindowFactory.createLogWindow();
        addWindow(logWindow);
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
                buttons[1]
        );

        if (result == 0) {
            // Очищаем ресурсы перед выходом
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