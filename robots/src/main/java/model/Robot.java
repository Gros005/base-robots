package model;

import java.awt.Point;

/**
 * Модель робота.
 */
public class Robot {
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    private double positionX;
    private double positionY;
    private double direction;
    private Point target;


    public Robot() {
        this(100, 100, 150, 100);
    }

    public Robot(double startX, double startY, int targetX, int targetY) {
        this.positionX = startX;
        this.positionY = startY;
        this.direction = 0;
        this.target = new Point(targetX, targetY);
    }

    /**
     * Переместить робота с заданными параметрами
     */
    public void move(double velocity, double angularVelocity, double duration) {
        // Ограничиваем значения
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        // Вычисляем новую позицию
        double newX = positionX + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = positionX + velocity * duration * Math.cos(direction);
        }

        double newY = positionY - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = positionY + velocity * duration * Math.sin(direction);
        }

        this.positionX = newX;
        this.positionY = newY;
        this.direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    /**
     * Установить новую цель
     */
    public void setTarget(Point newTarget) {
        this.target = new Point(newTarget);
    }

    /**
     * Сбросить робота в начальное состояние
     */
    public void reset() {
        this.positionX = 100;
        this.positionY = 100;
        this.direction = 0;
        this.target = new Point(150, 100);
    }

    /**
     * Расстояние до текущей цели
     */
    public double getDistanceToTarget() {
        double diffX = target.x - positionX;
        double diffY = target.y - positionY;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Угол до текущей цели
     */
    public double getAngleToTarget() {
        double diffX = target.x - positionX;
        double diffY = target.y - positionY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getDirection() {
        return direction;
    }

    public Point getTarget() {
        return new Point(target);
    }

    public double getMaxVelocity() {
        return MAX_VELOCITY;
    }

    public double getMaxAngularVelocity() {
        return MAX_ANGULAR_VELOCITY;
    }
}