package gui;

import model.Robot;
import service.RobotMovementService;
import listener.RobotMovementListener;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Визуализатор игрового поля(отрисовка).
 */
public class GameVisualizer extends JPanel implements RobotMovementListener {
    private final Robot robot;
    private final RobotMovementService movementService;

    private static final int REDRAW_DELAY_MS = 50;  // 20 кадров в секунду
    private static final int ROBOT_WIDTH = 30;
    private static final int ROBOT_HEIGHT = 10;
    private static final int ROBOT_EYE_OFFSET = 10;
    private static final int ROBOT_EYE_SIZE = 5;
    private static final int TARGET_SIZE = 5;

    /**
     * @param robot робот, которого нужно рисовать
     * @param movementService сервис, управляющий движением
     */
    public GameVisualizer(Robot robot, RobotMovementService movementService) {
        this.robot = robot;
        this.movementService = movementService;

        this.movementService.addListener(this);

        setDoubleBuffered(true);
        setupMouseListener();

        // Таймер для перерисовки
        Timer redrawTimer = new Timer(REDRAW_DELAY_MS, e -> repaint());
        redrawTimer.start();
    }

    /**
     * Настройка обработчика кликов
     */
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Передаем цель в сервис движения
                movementService.setTarget(e.getPoint());
                repaint();
            }
        });
    }

    /**
     * Сброс робота в начальное положение
     */
    public void resetRobot() {
        movementService.resetRobot();
        Logger.debug("Робот сброшен в начальную позицию");
    }

    /**
     * Остановка сервиса при закрытии
     */
    public void shutdown() {
        movementService.shutdown();
    }

    @Override
    public void onRobotMoved(Robot robot) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawRobot(g2d);
        drawTarget(g2d);
    }

    private void drawRobot(Graphics2D g) {
        int x = (int) Math.round(robot.getPositionX());
        int y = (int) Math.round(robot.getPositionY());
        double direction = robot.getDirection();

        // Сохраняем текущую трансформацию
        AffineTransform oldTransform = g.getTransform();

        // Поворачиваем контекст для отрисовки робота
        AffineTransform transform = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(transform);

        // Рисуем корпус робота
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, ROBOT_WIDTH, ROBOT_HEIGHT);

        // Рисуем контур корпуса
        g.setColor(Color.BLACK);
        drawOval(g, x, y, ROBOT_WIDTH, ROBOT_HEIGHT);

        // Рисуем "глаз" робота
        g.setColor(Color.WHITE);
        fillOval(g, x + ROBOT_EYE_OFFSET, y, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);

        // Рисуем контур глаза
        g.setColor(Color.BLACK);
        drawOval(g, x + ROBOT_EYE_OFFSET, y, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);

        // Восстанавливаем трансформацию
        g.setTransform(oldTransform);
    }

    private void drawTarget(Graphics2D g) {
        Point target = robot.getTarget();

        g.setColor(Color.GREEN);
        fillOval(g, target.x, target.y, TARGET_SIZE, TARGET_SIZE);

        g.setColor(Color.BLACK);
        drawOval(g, target.x, target.y, TARGET_SIZE, TARGET_SIZE);
    }

    private void fillOval(Graphics g, int x, int y, int w, int h) {
        g.fillOval(x - w/2, y - h/2, w, h);
    }

    private void drawOval(Graphics g, int x, int y, int w, int h) {
        g.drawOval(x - w/2, y - h/2, w, h);
    }
}