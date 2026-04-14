package model;

import java.awt.Point;

/**
 * Модель робота.
 */
public class Robot {

    private static final int DEFAULT_START_X = 100;
    private static final int DEFAULT_START_Y = 100;
    private static final int DEFAULT_TARGET_X = 150;
    private static final int DEFAULT_TARGET_Y = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    private static final double STOP_DISTANCE = 0.5;

    private static final int MARGIN = 20;

    // Динамические границы
    private double minX = 20;
    private double maxX = 780;
    private double minY = 20;
    private double maxY = 580;

    private double x;
    private double y;
    private double direction;
    private Point target;
    private boolean stopped;

    public Robot() {
        this(DEFAULT_START_X, DEFAULT_START_Y, DEFAULT_TARGET_X, DEFAULT_TARGET_Y);
    }

    public Robot(double x, double y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.direction = 0;
        this.target = new Point(targetX, targetY);
        this.stopped = false;
    }

    /**
     * Обновляет границы в соответствии с размером поля
     */
    public void updateBounds(int fieldWidth, int fieldHeight) {
        // Защита от некорректных размеров
        if (fieldWidth <= MARGIN * 2 || fieldHeight <= MARGIN * 2) {
            return;
        }
        this.minX = MARGIN;
        this.maxX = fieldWidth - MARGIN;
        this.minY = MARGIN;
        this.maxY = fieldHeight - MARGIN;

        // Корректируем текущую позицию
        x = clamp(x, minX, maxX);
        y = clamp(y, minY, maxY);

        int tx = (int) clamp(target.x, minX, maxX);
        int ty = (int) clamp(target.y, minY, maxY);
        target = new Point(tx, ty);
    }

    /**
     * Один шаг движения
     */
    public boolean moveOneStep() {
        if (stopped) {return false; }

        double distance = getDistanceToTarget();

        if (distance < STOP_DISTANCE) {
            x = target.x;
            y = target.y;
            stopped = true;
            return true;
        }

        // Вычисляем угол до цели
        double angleToTarget = getAngleToTarget();
        double angleDiff = angleToTarget - direction;

        // Нормализуем угол
        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

        // Определяем угловую скорость
        double angularVelocity = 0;
        if (angleDiff > 0) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDiff < 0) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        double velocity = MAX_VELOCITY;

        double newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * 10) - Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = x + velocity * 10 * Math.cos(direction);
        }

        double newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * 10) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * 10 * Math.sin(direction);
        }

        direction += angularVelocity * 10;
        while (direction < 0) direction += 2 * Math.PI;
        while (direction >= 2 * Math.PI) direction -= 2 * Math.PI;

        x = clamp(newX, minX, maxX);
        y = clamp(newY, minY, maxY);

        return true;
    }

    public void setTarget(Point newTarget) {
        int tx = (int) clamp(newTarget.x, minX, maxX);
        int ty = (int) clamp(newTarget.y, minY, maxY);
        this.target = new Point(tx, ty);
        this.stopped = false;
    }

    public void reset() {
        this.x = DEFAULT_START_X;
        this.y = DEFAULT_START_Y;
        this.direction = 0;
        this.target = new Point(DEFAULT_TARGET_X, DEFAULT_TARGET_Y);
        this.stopped = false;
    }

    public double getDistanceToTarget() {
        double dx = target.x - x;
        double dy = target.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getAngleToTarget() {
        double dx = target.x - x;
        double dy = target.y - y;
        return Math.atan2(dy, dx);
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public Point getTarget() { return new Point(target); }
    public boolean isStopped() { return stopped; }

    // Совместимость с GameVisualizer
    public double getPositionX() { return x; }
    public double getPositionY() { return y; }
    public double getMaxVelocity() { return MAX_VELOCITY; }
    public double getMaxAngularVelocity() { return MAX_ANGULAR_VELOCITY; }
}