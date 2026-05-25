package gui;

import config.ColorSettings;
import model.Robot;
import service.RobotMovementService;
import listener.RobotMovementListener;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SingleRobotPanel extends AbstractRobotPanel implements RobotMovementListener {
    private final Robot robot;
    private final RobotMovementService movementService;
    private final List<Point> trail = new ArrayList<>();
    private CoordinateWindow coordinateWindow;

    public SingleRobotPanel() {
        super();
        this.robot = new Robot();
        this.movementService = new RobotMovementService(robot);
        this.movementService.addListener(this);
        setupMouseListener();
        new Timer(50, e -> repaint()).start();
        Logger.debug("SingleRobotPanel создан");
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                movementService.setTarget(e.getPoint());
                repaint();
            }
        });
    }

    @Override
    protected void updateBounds(int width, int height) {
        robot.updateBounds(width, height);
    }

    public void setCoordinateWindow(CoordinateWindow window) {
        this.coordinateWindow = window;
        if (window != null) {
            movementService.addListener(window);
            window.setRobot(robot);
        }
    }

    public void resetRobot() {
        movementService.resetRobot();
        trail.clear();
        repaint();
    }

    public void shutdown() {
        movementService.shutdown();
    }

    @Override
    public void onRobotMoved(Robot robot) {
        trail.add(new Point((int) robot.getX(), (int) robot.getY()));
        if (coordinateWindow != null) { coordinateWindow.updateCoordinates(robot);}
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTrail(g2d, trail, ColorSettings.getInstance().getTrailColor());
        drawTarget(g2d, robot.getTarget());
        drawRobot(g2d, robot, ColorSettings.getInstance().getRobotColor());
    }
}