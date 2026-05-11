package gui;

import config.ColorSettings;
import model.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Общая логика отрисовки и работы с границами.
 */
public abstract class AbstractRobotPanel extends JPanel {

    protected static final int ROBOT_WIDTH = 30;
    protected static final int ROBOT_HEIGHT = 10;
    protected static final int ROBOT_EYE_SIZE = 5;
    protected static final int TARGET_SIZE = 5;

    public AbstractRobotPanel() {
        setDoubleBuffered(true);
        setupComponentListener();
    }

    private void setupComponentListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() > 0 && getHeight() > 0) {
                    updateBounds(getWidth(), getHeight());
                }
            }
        });
    }

    /**
     * Обновляет границы для всех роботов
     */
    protected abstract void updateBounds(int width, int height);

    /**
     * Рисует одного робота
     */
    protected void drawRobot(Graphics2D g, Robot robot, Color color) {
        int x = (int) Math.round(robot.getX());
        int y = (int) Math.round(robot.getY());
        double direction = robot.getDirection();

        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(direction);

        g.setColor(color);
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

    /**
     * Рисует цель
     */
    protected void drawTarget(Graphics2D g, Point target) {
        if (target == null) return;
        g.setColor(ColorSettings.getInstance().getTargetColor());
        fillOval(g, target.x, target.y);
        g.setColor(Color.BLACK);
        drawOval(g, target.x, target.y);
    }

    /**
     * Рисует след
     */
    protected void drawTrail(Graphics2D g, List<Point> trail, Color color) {
        if (trail == null || trail.size() < 2) return;
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        for (int i = 0; i < trail.size() - 1; i++) {
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void fillOval(Graphics g, int x, int y) {
        g.fillOval(x - AbstractRobotPanel.TARGET_SIZE /2, y - AbstractRobotPanel.TARGET_SIZE /2, AbstractRobotPanel.TARGET_SIZE, AbstractRobotPanel.TARGET_SIZE);
    }

    private void drawOval(Graphics g, int x, int y) {
        g.drawOval(x - AbstractRobotPanel.TARGET_SIZE /2, y - AbstractRobotPanel.TARGET_SIZE /2, AbstractRobotPanel.TARGET_SIZE, AbstractRobotPanel.TARGET_SIZE);
    }
}