package service;

import listener.RobotMovementListener;
import model.Robot;
import plugin.DefaultRobotController;
import plugin.RobotController;

import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Сервис, управляющий движением робота.
 */
public class RobotMovementService {
    private static final int MOVEMENT_INTERVAL_MS = 10;
    private static final int MOVEMENT_STEP_DURATION = 10;

    private final Robot robot;
    private final RobotController controller;
    private final Timer timer;
    private final CopyOnWriteArrayList<RobotMovementListener> listeners;

    public RobotMovementService(Robot robot) {
        this(robot, new DefaultRobotController());
    }

    public RobotMovementService(Robot robot, RobotController controller) {
        this.robot = robot;
        this.controller = controller;
        this.listeners = new CopyOnWriteArrayList<>();
        this.timer = new Timer("RobotMovementTimer", true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRobotPosition();
            }
        }, 0, MOVEMENT_INTERVAL_MS);
    }

    public void addListener(RobotMovementListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(RobotMovementListener listener) {
        listeners.remove(listener);
    }

    private void updateRobotPosition() {
        controller.update(robot, MOVEMENT_STEP_DURATION);
        notifyListeners();
    }

    private void notifyListeners() {
        for (RobotMovementListener listener : listeners) {
            try {
                listener.onRobotMoved(robot);
            } catch (Exception exception) {
                System.err.println("Error notifying listener: " + exception.getMessage());
            }
        }
    }

    public void setTarget(Point target) {
        robot.setTarget(target);
    }

    public void resetRobot() {
        robot.reset();
        notifyListeners();
    }

    public void shutdown() {
        timer.cancel();
        listeners.clear();
    }
}
