package gui;

import config.RobotColor;
import model.Robot;
import service.RobotMovementService;
import listener.RobotMovementListener;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Визуализатор игрового поля
 */
public class GameVisualizer extends JPanel implements RobotMovementListener {
    private final Robot robot;
    private final RobotMovementService movementService;
    private final List<Point> trail = new ArrayList<>();

    private static final int REDRAW_DELAY_MS = 50;
    private static final int ROBOT_WIDTH = 30;
    private static final int ROBOT_HEIGHT = 10;
    private static final int ROBOT_EYE_OFFSET = 10;
    private static final int ROBOT_EYE_SIZE = 5;
    private static final int TARGET_SIZE = 5;

    public GameVisualizer(Robot robot, RobotMovementService movementService) {
        this.robot = robot;
        this.movementService = movementService;

        this.movementService.addListener(this);

        setDoubleBuffered(true);
        setupMouseListener();

        Timer redrawTimer = new Timer(REDRAW_DELAY_MS, e -> repaint());
        redrawTimer.start();
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

    public void resetRobot() {
        movementService.resetRobot();
        clearTrail();
        Logger.debug("Робот сброшен в начальную позицию");
    }

    public void shutdown() {
        movementService.shutdown();
    }

    public void clearTrail() {
        trail.clear();
    }

    public Robot getRobot() {
        return robot;
    }

    public RobotMovementService getMovementService() {
        return movementService;
    }

    @Override
    public void onRobotMoved(Robot robot) {
        Point currentPos = new Point((int) robot.getPositionX(), (int) robot.getPositionY());
        trail.add(currentPos);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTrail(g2d);
        drawRobot(g2d);
        drawTarget(g2d);
    }

    private void drawRobot(Graphics2D g) {
        int x = (int) Math.round(robot.getPositionX());
        int y = (int) Math.round(robot.getPositionY());
        double direction = robot.getDirection();

        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(direction);

        g.setColor(RobotColor.getRobotColor());
        g.fillOval(-ROBOT_WIDTH/2, -ROBOT_HEIGHT/2, ROBOT_WIDTH, ROBOT_HEIGHT);

        g.setColor(Color.BLACK);
        g.drawOval(-ROBOT_WIDTH/2, -ROBOT_HEIGHT/2, ROBOT_WIDTH, ROBOT_HEIGHT);

        int eyeX = ROBOT_WIDTH/3;
        g.setColor(Color.WHITE);
        g.fillOval(eyeX - ROBOT_EYE_SIZE/2, -ROBOT_EYE_SIZE/2, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);

        g.setColor(Color.BLACK);
        g.drawOval(eyeX - ROBOT_EYE_SIZE/2, -ROBOT_EYE_SIZE/2, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);

        g.setTransform(old);
    }

    private void drawTarget(Graphics2D g) {
        Point target = robot.getTarget();

        g.setColor(RobotColor.getTargetColor());
        fillOval(g, target.x, target.y, TARGET_SIZE, TARGET_SIZE);

        g.setColor(Color.BLACK);
        drawOval(g, target.x, target.y, TARGET_SIZE, TARGET_SIZE);
    }

    private void drawTrail(Graphics2D g) {
        if (trail.size() < 2) return;

        g.setColor(RobotColor.getTrailColor());
        g.setStroke(new BasicStroke(2));

        for (int i = 0; i < trail.size() - 1; i++) {
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    private void fillOval(Graphics g, int x, int y, int w, int h) {
        g.fillOval(x - w/2, y - h/2, w, h);
    }

    private void drawOval(Graphics g, int x, int y, int w, int h) {
        g.drawOval(x - w/2, y - h/2, w, h);
    }
}