package service;

import model.Robot;
import listener.RobotMovementListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

public class RobotMovementServiceTest {

    private Robot robot;
    private RobotMovementService service;
    private TestRobotListener listener;

    @BeforeEach
    public void setUp() {
        robot = new Robot();
        service = new RobotMovementService(robot);
        listener = new TestRobotListener();
    }

    @AfterEach
    public void tearDown() {
        service.shutdown();
    }

    @Test
    public void testAddListener() {
        service.addListener(listener);
        service.setTarget(new Point(200, 100));

        // Даем время на движение
        sleep(100);

        assertTrue(listener.getCallCount() > 0);
    }

    @Test
    public void testRemoveListener() {
        service.addListener(listener);
        service.removeListener(listener);

        int initialCount = listener.getCallCount();
        service.setTarget(new Point(200, 100));

        sleep(100);

        assertEquals(initialCount, listener.getCallCount());
    }

    @Test
    public void testSetTarget() {
        Point target = new Point(300, 400);
        service.setTarget(target);

        Point robotTarget = robot.getTarget();
        assertEquals(300, robotTarget.x);
        assertEquals(400, robotTarget.y);
    }

    @Test
    public void testResetRobot() {
        service.setTarget(new Point(500, 500));
        sleep(100);

        double movedX = robot.getPositionX();
        double movedY = robot.getPositionY();

        assertNotEquals(100.0, movedX);

        service.resetRobot();

        assertEquals(100.0, robot.getPositionX(), 0.001);
        assertEquals(100.0, robot.getPositionY(), 0.001);
    }

    @Test
    public void testMultipleListeners() {
        TestRobotListener listener2 = new TestRobotListener();

        service.addListener(listener);
        service.addListener(listener2);

        service.setTarget(new Point(200, 100));
        sleep(100);

        assertTrue(listener.getCallCount() > 0);
        assertTrue(listener2.getCallCount() > 0);
        assertEquals(listener.getCallCount(), listener2.getCallCount());
    }

    @Test
    public void testShutdown() {
        service.addListener(listener);
        service.shutdown();

        int beforeCount = listener.getCallCount();

        service.setTarget(new Point(200, 100));
        sleep(100);

        assertEquals(beforeCount, listener.getCallCount());
    }

    @Test
    public void testNullListener() {
        assertDoesNotThrow(() -> service.addListener(null));
        assertDoesNotThrow(() -> service.removeListener(null));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Вспомогательный класс для тестирования
    private static class TestRobotListener implements RobotMovementListener {
        private int callCount = 0;
        private Robot lastRobot = null;

        @Override
        public void onRobotMoved(Robot robot) {
            callCount++;
            lastRobot = robot;
        }

        public int getCallCount() {
            return callCount;
        }

        public Robot getLastRobot() {
            return lastRobot;
        }

        public void reset() {
            callCount = 0;
            lastRobot = null;
        }
    }
}