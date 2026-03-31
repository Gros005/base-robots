package service;

import model.Robot;
import listener.RobotMovementListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class RobotMovementService {
    private final Robot robot;
    private final Timer timer;
    private final CopyOnWriteArrayList<RobotMovementListener> listeners;

    private static final int MOVEMENT_INTERVAL_MS = 10;  // как в оригинале

    public RobotMovementService(Robot robot) {
        this.robot = robot;
        this.listeners = new CopyOnWriteArrayList<>();
        this.timer = new Timer("RobotMovementTimer", true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean moved = robot.moveOneStep();
                if (moved) {
                    notifyListeners();
                }
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

    private void notifyListeners() {
        for (RobotMovementListener listener : listeners) {
            try {
                listener.onRobotMoved(robot);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    public void setTarget(java.awt.Point target) {
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