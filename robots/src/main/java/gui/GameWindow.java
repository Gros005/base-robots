package gui;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 * Окно с игровым полем контейнер для GameVisualizer.
 */
public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    /**
     * @param visualizer визуализатор для отображения
     */
    public GameWindow(GameVisualizer visualizer) {
        super("Игровое поле", true, true, true, true);
        this.m_visualizer = visualizer;

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        pack();
    }

    /**
     * Визуализатор для доступа из других классов
     */
    public GameVisualizer getVisualizer() {
        return m_visualizer;
    }
}