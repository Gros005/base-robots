package factory;

import gui.GameWindow;
import gui.Language;
import gui.MainApplicationFrame;
import log.Logger;
import config.ColorSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Создание меню приложения.
 */
public class MenuBarFactory {
    private final MainApplicationFrame parentFrame;
    private final List<GameWindow> allGameWindows;

    public MenuBarFactory(MainApplicationFrame parentFrame, GameWindow firstGameWindow) {
        this.parentFrame = parentFrame;
        this.allGameWindows = new ArrayList<>();
        this.allGameWindows.add(firstGameWindow);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createRobotColorMenu());
        menuBar.add(createTargetColorMenu());
        menuBar.add(createTrailColorMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createTestsMenu());
        menuBar.add(createWindowsMenu());

        return menuBar;
    }

    private JMenuItem createColorMenuItem(String colorKey, ColorSettings.ColorPreset preset, Consumer<ColorSettings.ColorPreset> setter) {
        JMenuItem item = new JMenuItem(Language.get(colorKey));
        item.addActionListener(e -> {
            setter.accept(preset);
            updateColors();
        });
        return item;
    }

    private JMenu createColorSubmenu(String menuTitleKey, Consumer<ColorSettings.ColorPreset> setter) {
        JMenu menu = new JMenu(Language.get(menuTitleKey));

        menu.add(createColorMenuItem("menu.color.red", ColorSettings.ColorPreset.RED, setter));
        menu.add(createColorMenuItem("menu.color.blue", ColorSettings.ColorPreset.BLUE, setter));
        menu.add(createColorMenuItem("menu.color.green", ColorSettings.ColorPreset.GREEN, setter));
        menu.add(createColorMenuItem("menu.color.yellow", ColorSettings.ColorPreset.YELLOW, setter));
        menu.add(createColorMenuItem("menu.color.purple", ColorSettings.ColorPreset.PURPLE, setter));

        return menu;
    }

    private JMenu createRobotColorMenu() {
        JMenu menu = createColorSubmenu("menu.color", preset ->
                ColorSettings.getInstance().setRobotColor(preset));
        menu.setMnemonic(KeyEvent.VK_R);
        return menu;
    }

    private JMenu createTargetColorMenu() {
        JMenu menu = createColorSubmenu("menu.targetColor", preset ->
                ColorSettings.getInstance().setTargetColor(preset));
        menu.setMnemonic(KeyEvent.VK_T);
        return menu;
    }

    private JMenu createTrailColorMenu() {
        JMenu menu = createColorSubmenu("menu.trailColor", preset ->
                ColorSettings.getInstance().setTrailColor(preset));

        JMenuItem grayItem = new JMenuItem(Language.get("menu.trailColor.gray"));
        grayItem.addActionListener(e -> {
            ColorSettings.getInstance().setTrailColor(ColorSettings.ColorPreset.GRAY);
            updateColors();
        });
        menu.add(grayItem);

        menu.setMnemonic(KeyEvent.VK_L);
        return menu;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu(Language.get("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newItem = new JMenuItem(Language.get("menu.file.new"));
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK));
        newItem.addActionListener(this::onNew);
        menu.add(newItem);

        menu.addSeparator();

        JMenuItem quitItem = new JMenuItem(Language.get("menu.file.quit"));
        quitItem.setMnemonic(KeyEvent.VK_Q);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK));
        quitItem.addActionListener(e -> {
            parentFrame.dispatchEvent(new java.awt.event.WindowEvent(
                    parentFrame, java.awt.event.WindowEvent.WINDOW_CLOSING
            ));
        });
        menu.add(quitItem);

        return menu;
    }

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

    private JMenu createTestsMenu() {
        JMenu menu = new JMenu(Language.get("menu.tests"));
        menu.setMnemonic(KeyEvent.VK_T);

        JMenuItem addLogItem = new JMenuItem(Language.get("menu.tests.addLog"));
        addLogItem.addActionListener(e -> Logger.debug("Новая строка"));
        menu.add(addLogItem);

        return menu;
    }

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

    private void updateWindowsMenu(JMenu windowsMenu) {
        while (windowsMenu.getItemCount() > 2) {
            windowsMenu.remove(2);
        }

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

    private void closeAllGameWindows() {
        for (GameWindow window : allGameWindows) {
            window.dispose();
        }
        allGameWindows.clear();
        Logger.debug("Все окна закрыты");
    }

    private void onNew(ActionEvent e) {
        GameWindow newWindow = WindowFactory.createNewGameWindow();
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

    private void updateUILanguage() {
        parentFrame.setJMenuBar(createMenuBar());
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private void updateColors() {
        for (GameWindow window : allGameWindows) {
            if (window != null && window.getVisualizer() != null) {
                window.getVisualizer().repaint();
            }
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(parentFrame);
        } catch (Exception e) {
            // Игнорируем ошибки смены темы
        }
    }
}