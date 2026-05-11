package service;

import model.Robot;
import listener.RobotMovementListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

public class RobotMovementServiceTest {

    private Robot robot;
    private RobotMovementService service;

    @BeforeEach
    public void setUp() {
        robot = new Robot();
        robot.updateBounds(800, 600);
        service = new RobotMovementService(robot);
    }

    @Test
    public void testAddListener() {
        AtomicBoolean notified = new AtomicBoolean(false);

        RobotMovementListener listener = r -> notified.set(true);
        service.addListener(listener);

        service.setTarget(new Point(200, 100));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(notified.get());
    }

    @Test
    public void testRemoveListener() {
        AtomicBoolean notified = new AtomicBoolean(false);

        RobotMovementListener listener = r -> notified.set(true);
        service.addListener(listener);
        service.removeListener(listener);

        service.setTarget(new Point(200, 100));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(notified.get());
    }

    @Test
    public void testSetTarget() {
        Point target = new Point(500, 400);
        service.setTarget(target);

        Point robotTarget = robot.getTarget();
        assertEquals(500, robotTarget.x);
        assertEquals(400, robotTarget.y);
        assertFalse(robot.isStopped());
    }

    @Test
    public void testResetRobot() {
        service.setTarget(new Point(500, 400));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        double movedX = robot.getX();
        double movedY = robot.getY();

        assertTrue(movedX != 100 || movedY != 100);

        service.resetRobot();

        assertEquals(100.0, robot.getX(), 0.001);
        assertEquals(100.0, robot.getY(), 0.001);
        assertFalse(robot.isStopped());
    }

    @Test
    public void testMultipleListeners() {
        AtomicBoolean notified1 = new AtomicBoolean(false);
        AtomicBoolean notified2 = new AtomicBoolean(false);

        service.addListener(r -> notified1.set(true));
        service.addListener(r -> notified2.set(true));

        service.setTarget(new Point(200, 100));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(notified1.get());
        assertTrue(notified2.get());
    }

    @Test
    public void testShutdown() {
        AtomicBoolean notified = new AtomicBoolean(false);
        service.addListener(r -> notified.set(true));

        service.shutdown();

        service.setTarget(new Point(200, 100));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(notified.get());
    }
}