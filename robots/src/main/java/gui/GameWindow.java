package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Окно с игровым полем.
 */
public class GameWindow extends JInternalFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final SingleRobotPanel singlePanel;
    private final RacePanel racePanel;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);

        singlePanel = new SingleRobotPanel();
        racePanel = new RacePanel();

        mainPanel.add(singlePanel, "SINGLE");
        mainPanel.add(racePanel, "RACE");

        getContentPane().add(mainPanel);
        setSize(400, 400);
    }

    public void switchToSingleMode() {
        cardLayout.show(mainPanel, "SINGLE");
        setTitle("Игровое поле (обычный режим)");
    }

    public void switchToRaceMode(int robotCount, RaceSetupDialog.PlacementMode mode) {
        racePanel.setupRace(robotCount, mode);
        cardLayout.show(mainPanel, "RACE");
        setTitle("Игровое поле (гонка)");
    }

    public SingleRobotPanel getSinglePanel() {
        return singlePanel;
    }

    public void resetRobot() {
        singlePanel.resetRobot();
    }

    public void shutdown() {
        singlePanel.shutdown();
        racePanel.shutdown();
    }
}