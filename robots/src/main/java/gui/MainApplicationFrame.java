package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateManager windowStateManager = new WindowStateManager();

    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        logWindow.setName("logWindow");
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setName("gameWindow");
        addWindow(gameWindow);

        windowStateManager.restore(this, logWindow, gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowStateManager.save(MainApplicationFrame.this, logWindow, gameWindow);
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        lw.setSize(300, 600);
        lw.pack();
        return lw;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu visualMenu = new JMenu("Режим отображения");
        addMenuItem(visualMenu, "Системная схема", UIManager.getSystemLookAndFeelClassName());
        addMenuItem(visualMenu, "Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName());
        menuBar.add(visualMenu);
        return menuBar;
    }

    private void addMenuItem(JMenu menu, String label, String className) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            try {
                UIManager.setLookAndFeel(className);
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                Logger.error(ex.getMessage());
            }
        });
        menu.add(item);
    }
}