package service;

import model.Robot;
import listener.RobotMovementListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Сервис, управляющий движением робота
 */
public class RobotMovementService {
    private final Robot robot;
    private final Timer timer;
    private final CopyOnWriteArrayList<RobotMovementListener> listeners;

    /**
     * Создает сервис движения для конкретного робота
     * @param robot робот, которым нужно управлять
     */
    public RobotMovementService(Robot robot) {
        this.robot = robot;
        this.listeners = new CopyOnWriteArrayList<>();
        this.timer = new Timer("RobotMovementTimer", true); // daemon thread

        // Запускаем таймер на обновление позиции каждые 10 мс
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRobotPosition();
            }
        }, 0, 10);
    }

    /**
     * Добавляет слушателя
     */
    public void addListener(RobotMovementListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Удаляет слушателя
     */
    public void removeListener(RobotMovementListener listener) {
        listeners.remove(listener);
    }

    /**
     * Обновляет позицию робота на основе текущей цели
     */
    private void updateRobotPosition() {
        // Если робот уже близко к цели - не двигаемся
        if (robot.getDistanceToTarget() < 0.5) {
            return;
        }

        double velocity = robot.getMaxVelocity();
        double angleToTarget = robot.getAngleToTarget();
        double currentDirection = robot.getDirection();

        double angularVelocity = 0;
        if (angleToTarget > currentDirection) {
            angularVelocity = robot.getMaxAngularVelocity();
        } else if (angleToTarget < currentDirection) {
            angularVelocity = -robot.getMaxAngularVelocity();
        }

        robot.move(velocity, angularVelocity, 10);
        notifyListeners();
    }

    /**
     * Уведомляет всех слушателей о движении робота
     */
    private void notifyListeners() {
        for (RobotMovementListener listener : listeners) {
            try {
                listener.onRobotMoved(robot);
            } catch (Exception e) {
                // Логируем ошибку, но не даем ей остановить другие уведомления
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    /**
     * Устанавливает новую цель для робота
     */
    public void setTarget(java.awt.Point target) {
        robot.setTarget(target);
    }

    /**
     * Сбрасывает робота в начальное состояние
     */
    public void resetRobot() {
        robot.reset();
        notifyListeners();
    }

    /**
     * Останавливает сервис
     */
    public void shutdown() {
        timer.cancel();
        listeners.clear();
    }
}