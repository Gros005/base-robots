package gui;

import listener.RobotMovementListener;
import log.Logger;
import model.Robot;
import plugin.DefaultRobotRenderer;
import plugin.RobotRenderer;
import service.RobotMovementService;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Визуализатор игрового поля.
 */
public class GameVisualizer extends JPanel implements RobotMovementListener {
    private static final int REDRAW_DELAY_MS = 50;
    private static final int TARGET_SIZE = 5;

    private final Robot robot;
    private final RobotMovementService movementService;
    private final RobotRenderer robotRenderer;

    public GameVisualizer(Robot robot, RobotMovementService movementService) {
        this(robot, movementService, new DefaultRobotRenderer());
    }

    public GameVisualizer(Robot robot, RobotMovementService movementService, RobotRenderer robotRenderer) {
        this.robot = robot;
        this.movementService = movementService;
        this.robotRenderer = robotRenderer;

        this.movementService.addListener(this);
        setDoubleBuffered(true);
        setupMouseListener();

        Timer redrawTimer = new Timer(REDRAW_DELAY_MS, event -> repaint());
        redrawTimer.start();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                movementService.setTarget(event.getPoint());
                repaint();
            }
        });
    }

    public void resetRobot() {
        movementService.resetRobot();
        Logger.debug("Робот сброшен в начальную позицию");
    }

    public void shutdown() {
        movementService.shutdown();
    }

    @Override
    public void onRobotMoved(Robot robot) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        robotRenderer.paint(graphics2D, robot);
        drawTarget(graphics2D);
    }

    private void drawTarget(Graphics2D graphics) {
        Point target = robot.getTarget();

        graphics.setColor(Color.GREEN);
        fillOval(graphics, target.x, target.y, TARGET_SIZE, TARGET_SIZE);

        graphics.setColor(Color.BLACK);
        drawOval(graphics, target.x, target.y, TARGET_SIZE, TARGET_SIZE);
    }

    private void fillOval(Graphics graphics, int x, int y, int width, int height) {
        graphics.fillOval(x - width / 2, y - height / 2, width, height);
    }

    private void drawOval(Graphics graphics, int x, int y, int width, int height) {
        graphics.drawOval(x - width / 2, y - height / 2, width, height);
    }
}
