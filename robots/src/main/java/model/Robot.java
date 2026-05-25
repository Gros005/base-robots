package model;

import java.awt.Point;

/**
 * Модель робота с оригинальной физикой движения по дуге.
 */
public class Robot {

    private static final int DEFAULT_START_X = 100;
    private static final int DEFAULT_START_Y = 100;
    private static final int DEFAULT_TARGET_X = 150;
    private static final int DEFAULT_TARGET_Y = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.003;
    private static final double STOP_DISTANCE = 2.0;  // Увеличен порог

    // Таймстеп движения (10 мс)
    private static final double DT = 10;

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

    public void updateBounds(int fieldWidth, int fieldHeight) {
        if (fieldWidth <= MARGIN * 2 || fieldHeight <= MARGIN * 2) {
            return;
        }
        this.minX = MARGIN;
        this.maxX = fieldWidth - MARGIN;
        this.minY = MARGIN;
        this.maxY = fieldHeight - MARGIN;

        x = clamp(x, minX, maxX);
        y = clamp(y, minY, maxY);

        int tx = (int) clamp(target.x, minX, maxX);
        int ty = (int) clamp(target.y, minY, maxY);
        target = new Point(tx, ty);
    }

    public boolean moveOneStep() {
        if (stopped) return false;

        double distance = getDistanceToTarget();

        if (distance < STOP_DISTANCE) {
            x = target.x;
            y = target.y;
            stopped = true;
            return true;
        }

        double angleToTarget = getAngleToTarget();
        double angleDiff = angleToTarget - direction;

        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

        double maxTurn = MAX_ANGULAR_VELOCITY * DT;

        if (Math.abs(angleDiff) < maxTurn) {
            direction = angleToTarget;
        } else if (angleDiff > 0) {
            direction += maxTurn;
        } else {
            direction -= maxTurn;
        }

        while (direction < 0) direction += 2 * Math.PI;
        while (direction >= 2 * Math.PI) direction -= 2 * Math.PI;

        double velocity = MAX_VELOCITY;
        if (distance < 50) {
            velocity = MAX_VELOCITY * (distance / 50);
            if (velocity < 0.3) velocity = 0.3;
        }

        double newX = x + velocity * DT * Math.cos(direction);
        double newY = y + velocity * DT * Math.sin(direction);

        double oldX = x, oldY = y;
        x = clamp(newX, minX, maxX);
        y = clamp(newY, minY, maxY);

        if (Math.abs(x - oldX) < 0.1 && Math.abs(y - oldY) < 0.1) {
            direction += Math.PI / 2;
            while (direction < 0) direction += 2 * Math.PI;
            while (direction >= 2 * Math.PI) direction -= 2 * Math.PI;
        }

        double newDistance = getDistanceToTarget();
        if (newDistance > distance && distance < 15) {
            x = target.x;
            y = target.y;
            stopped = true;
            return true;
        }

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

    // Геттеры
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public Point getTarget() { return new Point(target); }
    public boolean isStopped() { return stopped; }

    public double getPositionX() { return x; }
    public double getPositionY() { return y; }
    public double getMaxVelocity() { return MAX_VELOCITY; }
    public double getMaxAngularVelocity() { return MAX_ANGULAR_VELOCITY; }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}