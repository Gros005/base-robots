package factory;

import gui.GameWindow;
import gui.Language;
import gui.MainApplicationFrame;
import log.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
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

    public MenuBarFactory(MainApplicationFrame parentFrame, List<GameWindow> initialGameWindows) {
        this.parentFrame = parentFrame;
        this.allGameWindows = new ArrayList<>(initialGameWindows);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createTestsMenu());
        menuBar.add(createWindowsMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu(Language.get("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newItem = new JMenuItem(Language.get("menu.file.new"));
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK));
        newItem.addActionListener(this::onNew);
        menu.add(newItem);

        menu.addSeparator();

        JMenuItem quitItem = new JMenuItem(Language.get("menu.file.quit"));
        quitItem.setMnemonic(KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK));
        quitItem.addActionListener(event -> parentFrame.dispatchEvent(
            new java.awt.event.WindowEvent(parentFrame, java.awt.event.WindowEvent.WINDOW_CLOSING)
        ));
        menu.add(quitItem);

        return menu;
    }

    private JMenu createLanguageMenu() {
        JMenu menu = new JMenu(Language.get("menu.language"));
        menu.setMnemonic(KeyEvent.VK_L);

        JMenuItem russianItem = new JMenuItem(Language.get("menu.language.russian"));
        russianItem.addActionListener(event -> {
            Language.setLanguage(Language.Lang.RUSSIAN);
            updateUILanguage();
        });
        menu.add(russianItem);

        JMenuItem englishItem = new JMenuItem(Language.get("menu.language.english"));
        englishItem.addActionListener(event -> {
            Language.setLanguage(Language.Lang.ENGLISH);
            updateUILanguage();
        });
        menu.add(englishItem);

        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu = new JMenu(Language.get("menu.view.lookAndFeel"));
        menu.setMnemonic(KeyEvent.VK_V);

        JMenuItem systemItem = new JMenuItem(Language.get("menu.view.lookAndFeel.system"));
        systemItem.addActionListener(event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        menu.add(systemItem);

        JMenuItem crossItem = new JMenuItem(Language.get("menu.view.lookAndFeel.cross"));
        crossItem.addActionListener(event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        menu.add(crossItem);

        return menu;
    }

    private JMenu createTestsMenu() {
        JMenu menu = new JMenu(Language.get("menu.tests"));
        menu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogItem = new JMenuItem(Language.get("menu.tests.addLog"));
        addLogItem.addActionListener(event -> Logger.debug("Новая строка"));
        menu.add(addLogItem);

        return menu;
    }

    private JMenu createWindowsMenu() {
        JMenu menu = new JMenu("Окна");
        menu.setMnemonic(KeyEvent.VK_W);

        JMenuItem closeAllItem = new JMenuItem("Закрыть все");
        closeAllItem.addActionListener(event -> closeAllGameWindows());
        menu.add(closeAllItem);

        menu.addSeparator();
        updateWindowsMenu(menu);

        return menu;
    }

    private void updateWindowsMenu(JMenu windowsMenu) {
        while (windowsMenu.getItemCount() > 2) {
            windowsMenu.remove(2);
        }

        for (int i = 0; i < allGameWindows.size(); i++) {
            GameWindow window = allGameWindows.get(i);
            if (!window.isVisible()) {
                continue;
            }

            JMenuItem windowItem = new JMenuItem("Окно " + (i + 1));
            windowItem.addActionListener(event -> {
                try {
                    window.setSelected(true);
                    window.toFront();
                } catch (java.beans.PropertyVetoException ignored) {
                }
            });
            windowsMenu.add(windowItem);
        }
    }

    private void closeAllGameWindows() {
        for (GameWindow window : allGameWindows) {
            window.dispose();
        }
        allGameWindows.clear();
        Logger.debug("Все окна закрыты");
    }

    private void onNew(ActionEvent event) {
        GameWindow newWindow = WindowFactory.createNewGameWindow();
        parentFrame.addGameWindow(newWindow);
        allGameWindows.add(newWindow);

        Logger.debug("Создано новое окно с роботом");
        JOptionPane.showMessageDialog(
            parentFrame,
            "Создано новое окно с роботом",
            "Новое окно",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateUILanguage() {
        parentFrame.setJMenuBar(createMenuBar());
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(parentFrame);
        } catch (Exception ignored) {
        }
    }
}
