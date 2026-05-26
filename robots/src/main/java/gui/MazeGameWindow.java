package gui;

import maze.ColorChoiceDialog;
import maze.MazePanel;
import config.ColorSettings;
import config.ColorSettings.ColorPreset;

import javax.swing.*;

public class MazeGameWindow extends JInternalFrame {

    private MazePanel mazePanel;

    public MazeGameWindow() {
        super("Лабиринт", true, true, true, true);

        ColorChoiceDialog colorDialog = new ColorChoiceDialog(this);
        if (!colorDialog.isConfirmed()) {
            dispose();
            return;
        }

        ColorPreset player1Preset = colorDialog.getPlayer1Preset();
        ColorPreset player2Preset = colorDialog.getPlayer2Preset();

        ColorSettings.getInstance().setPlayer1Color(player1Preset);
        ColorSettings.getInstance().setPlayer2Color(player2Preset);

        this.mazePanel = new MazePanel();
        setContentPane(mazePanel);
        setSize(550, 580);

        mazePanel.setFocusable(true);
        mazePanel.requestFocusInWindow();
    }

    public void shutdown() {
        if (mazePanel != null) {
            mazePanel.stopGame();
        }
    }
}