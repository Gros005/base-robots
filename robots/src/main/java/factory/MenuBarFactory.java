package factory;

import gui.GameWindow;
import gui.Language;
import gui.MainApplicationFrame;
import log.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Создание меню приложения.
 */
public class MenuBarFactory {
    private final MainApplicationFrame parentFrame;
    private final List<GameWindow> allGameWindows;

    /**
     * @param parentFrame главное окно приложения
     * @param firstGameWindow первое окно с игрой
     */
    public MenuBarFactory(MainApplicationFrame parentFrame, GameWindow firstGameWindow) {
        this.parentFrame = parentFrame;
        this.allGameWindows = new ArrayList<>();
        this.allGameWindows.add(firstGameWindow);
    }

    /**
     * Создает строку меню
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createTestsMenu());
        menuBar.add(createWindowsMenu());

        return menuBar;
    }

    /**
     * Создает меню "Файл" с пунктами New и Quit
     */
    private JMenu createFileMenu() {
        JMenu menu = new JMenu(Language.get("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newItem = new JMenuItem(Language.get("menu.file.new"));
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK));
        newItem.addActionListener(this::onNew);
        menu.add(newItem);

        menu.addSeparator();

        JMenuItem quitItem = new JMenuItem(Language.get("menu.file.quit"));
        quitItem.setMnemonic(KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        quitItem.addActionListener(e -> {
            parentFrame.dispatchEvent(new java.awt.event.WindowEvent(
                    parentFrame, java.awt.event.WindowEvent.WINDOW_CLOSING
            ));
        });
        menu.add(quitItem);

        return menu;
    }

    /**
     * Создает меню выбора языка
     */
    private JMenu createLanguageMenu() {
        JMenu menu = new JMenu(Language.get("menu.language"));
        menu.setMnemonic(KeyEvent.VK_L);

        JMenuItem russianItem = new JMenuItem(Language.get("menu.language.russian"));
        russianItem.addActionListener(e -> {
            Language.setLanguage(Language.Lang.RUSSIAN);
            updateUILanguage();
        });
        menu.add(russianItem);

        JMenuItem englishItem = new JMenuItem(Language.get("menu.language.english"));
        englishItem.addActionListener(e -> {
            Language.setLanguage(Language.Lang.ENGLISH);
            updateUILanguage();
        });
        menu.add(englishItem);

        return menu;
    }

    /**
     * Создает меню "Режим отображения"
     */
    private JMenu createViewMenu() {
        JMenu menu = new JMenu(Language.get("menu.view.lookAndFeel"));
        menu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemItem = new JMenuItem(Language.get("menu.view.lookAndFeel.system"));
        systemItem.addActionListener(e -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        });
        menu.add(systemItem);

        JMenuItem crossItem = new JMenuItem(Language.get("menu.view.lookAndFeel.cross"));
        crossItem.addActionListener(e -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        });
        menu.add(crossItem);

        return menu;
    }

    /**
     * Создает тестовое меню
     */
    private JMenu createTestsMenu() {
        JMenu menu = new JMenu(Language.get("menu.tests"));
        menu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogItem = new JMenuItem(Language.get("menu.tests.addLog"));
        addLogItem.addActionListener(e -> {
            Logger.debug("Новая строка");
        });
        menu.add(addLogItem);

        return menu;
    }

    /**
     * Создает меню для управления окнами
     */
    private JMenu createWindowsMenu() {
        JMenu menu = new JMenu("Окна");
        menu.setMnemonic(KeyEvent.VK_W);

        JMenuItem closeAllItem = new JMenuItem("Закрыть все");
        closeAllItem.addActionListener(e -> closeAllGameWindows());
        menu.add(closeAllItem);

        menu.addSeparator();
        updateWindowsMenu(menu);

        return menu;
    }

    /**
     * Обновляет список окон в меню
     */
    private void updateWindowsMenu(JMenu windowsMenu) {
        // Очищаем старые пункты (кроме первых двух)
        while (windowsMenu.getItemCount() > 2) {
            windowsMenu.remove(2);
        }

        // Добавляем все активные окна
        for (int i = 0; i < allGameWindows.size(); i++) {
            GameWindow window = allGameWindows.get(i);
            if (window.isVisible()) {
                JMenuItem windowItem = new JMenuItem("Окно " + (i + 1));
                windowItem.addActionListener(e -> {
                    try {
                        window.setSelected(true);
                        window.toFront();
                    } catch (java.beans.PropertyVetoException ex) {
                        // Игнорируем
                    }
                });
                windowsMenu.add(windowItem);
            }
        }
    }

    /**
     * Закрывает все игровые окна
     */
    private void closeAllGameWindows() {
        for (GameWindow window : allGameWindows) {
            window.dispose();
        }
        allGameWindows.clear();
        Logger.debug("Все окна закрыты");
    }

    /**
     * Обработчик пункта меню New
     */
    private void onNew(ActionEvent e) {
        // Создаем новое окно через фабрику
        GameWindow newWindow = WindowFactory.createNewGameWindow();

        // Добавляем окно в главное окно
        parentFrame.addWindow(newWindow);
        allGameWindows.add(newWindow);

        Logger.debug("Создано новое окно с роботом");

        JOptionPane.showMessageDialog(
                parentFrame,
                "Создано новое окно с роботом",
                "Новое окно",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Обновляет язык в интерфейсе
     */
    private void updateUILanguage() {
        parentFrame.setJMenuBar(createMenuBar());
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    /**
     * Меняет Look and Feel приложения
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(parentFrame);
        } catch (Exception e) {
            // Игнорируем ошибки смены темы, тк они не критичны для работы программы
        }
    }
}