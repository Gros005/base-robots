package gui;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Окно с игровым полем.
 */
public class GameWindow extends JInternalFrame {
    private final GameVisualizer visualizer;
    private final Runnable closeAction;
    private final AtomicBoolean cleanedUp = new AtomicBoolean(false);

    public GameWindow(GameVisualizer visualizer) {
        this(visualizer, () -> {
        });
    }

    public GameWindow(GameVisualizer visualizer, Runnable closeAction) {
        super("Игровое поле", true, true, true, true);
        this.visualizer = visualizer;
        this.closeAction = closeAction;

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setupCleanupHandler();
        pack();
    }

    public GameVisualizer getVisualizer() {
        return visualizer;
    }

    private void setupCleanupHandler() {
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent event) {
                cleanupResources();
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent event) {
                cleanupResources();
            }
        });
    }

    private void cleanupResources() {
        if (!cleanedUp.compareAndSet(false, true)) {
            return;
        }

        visualizer.shutdown();
        try {
            closeAction.run();
        } catch (Exception ignored) {
        }
    }
}
