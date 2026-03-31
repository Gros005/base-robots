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

    private static final double MIN_X = 20;
    private static final double MAX_X = 780;
    private static final double MIN_Y = 20;
    private static final double MAX_Y = 580;

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

        x = clamp(newX, MIN_X, MAX_X);
        y = clamp(newY, MIN_Y, MAX_Y);

        return true;
    }

    public void setTarget(Point newTarget) {
        int tx = (int) clamp(newTarget.x, MIN_X, MAX_X);
        int ty = (int) clamp(newTarget.y, MIN_Y, MAX_Y);
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