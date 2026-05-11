package gui;

import model.Robot;
import listener.RobotMovementListener;
import javax.swing.*;
import java.awt.*;

public class CoordinateWindow extends JInternalFrame implements RobotMovementListener {
    private final Robot robot;
    private final JLabel xLabel;
    private final JLabel yLabel;
    private final JLabel angleLabel;
    private final JLabel targetLabel;
    private final JLabel statusLabel;

    public CoordinateWindow(Robot robot) {
        super("Координаты робота", true, true, true, true);
        this.robot = robot;

        setSize(280, 180);
        setLocation(400, 500);
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("X:"));
        xLabel = new JLabel("0.0");
        add(xLabel);

        add(new JLabel("Y:"));
        yLabel = new JLabel("0.0");
        add(yLabel);

        add(new JLabel("Угол:"));
        angleLabel = new JLabel("0.0°");
        add(angleLabel);

        add(new JLabel("Цель:"));
        targetLabel = new JLabel("(0, 0)");
        add(targetLabel);

        add(new JLabel("Статус:"));
        statusLabel = new JLabel("Движется");
        add(statusLabel);

        updateCoordinates();
    }

    @Override
    public void onRobotMoved(Robot robot) {
        SwingUtilities.invokeLater(this::updateCoordinates);
    }

    private void updateCoordinates() {
        xLabel.setText(String.format("%.1f", robot.getPositionX()));
        yLabel.setText(String.format("%.1f", robot.getPositionY()));

        double degrees = Math.toDegrees(robot.getDirection());
        angleLabel.setText(String.format("%.1f°", degrees));

        Point target = robot.getTarget();
        targetLabel.setText(String.format("(%d, %d)", target.x, target.y));

        statusLabel.setText(robot.isStopped() ? "У цели" : "Движется");
    }
}