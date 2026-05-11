package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

public class RobotTest {

    private Robot robot;

    @BeforeEach
    public void setUp() {
        robot = new Robot();
        robot.updateBounds(800, 600);
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(100.0, robot.getX(), 0.001);
        assertEquals(100.0, robot.getY(), 0.001);
        assertEquals(0.0, robot.getDirection(), 0.001);
        assertFalse(robot.isStopped());
    }

    @Test
    public void testSetTarget() {
        Point target = new Point(500, 400);
        robot.setTarget(target);

        Point result = robot.getTarget();
        assertEquals(500, result.x);
        assertEquals(400, result.y);
        assertFalse(robot.isStopped());
    }

    @Test
    public void testUpdateBounds() {
        robot.updateBounds(1024, 768);

        robot.setTarget(new Point(2000, 2000));
        Point target = robot.getTarget();
        assertTrue(target.x <= 1004);
        assertTrue(target.y <= 748);
    }

    @Test
    public void testMoveOneStep() {
        robot.setTarget(new Point(200, 100));
        double oldX = robot.getX();
        double oldY = robot.getY();

        boolean moved = robot.moveOneStep();

        assertTrue(moved);
        assertTrue(robot.getX() != oldX || robot.getY() != oldY);
    }

    @Test
    public void testStopsAtTarget() {
        robot.reset();
        robot.updateBounds(800, 600);

        double startX = robot.getX();
        double startY = robot.getY();
        robot.setTarget(new Point((int) startX, (int) startY));

        boolean moved = false;
        for (int i = 0; i < 10; i++) {
            if (robot.moveOneStep()) {
                moved = true;
            }
        }
        assertTrue(Math.abs(robot.getX() - startX) < 0.1, "Робот не должен значительно двигаться");
        assertTrue(Math.abs(robot.getY() - startY) < 0.1, "Робот не должен значительно двигаться");
        assertTrue(robot.isStopped(), "Робот должен быть остановлен");
    }

    @Test
    public void testReset() {
        robot.setTarget(new Point(500, 400));

        assertEquals(500, robot.getTarget().x);
        assertEquals(400, robot.getTarget().y);

        robot.reset();
        robot.updateBounds(800, 600);
        assertEquals(100.0, robot.getX(), 0.001);
        assertEquals(100.0, robot.getY(), 0.001);
        assertEquals(0.0, robot.getDirection(), 0.001);
        assertFalse(robot.isStopped());

        Point target = robot.getTarget();
        System.out.println("Target after reset: (" + target.x + ", " + target.y + ")");
        assertEquals(150, target.x, "Цель по X должна быть 150");
        assertEquals(100, target.y, "Цель по Y должна быть 100");
    }

    @Test
    public void testDistanceToTarget() {
        robot.setTarget(new Point(200, 100));
        assertEquals(100.0, robot.getDistanceToTarget(), 0.001);

        robot.setTarget(new Point(100, 200));
        assertEquals(100.0, robot.getDistanceToTarget(), 0.001);

        robot.setTarget(new Point(200, 200));
        assertEquals(141.421, robot.getDistanceToTarget(), 0.001);
    }

    @Test
    public void testAngleToTarget() {
        robot.setTarget(new Point(200, 100));
        assertEquals(0.0, robot.getAngleToTarget(), 0.001);

        robot.setTarget(new Point(100, 200));
        assertEquals(Math.PI/2, robot.getAngleToTarget(), 0.001);

        robot.setTarget(new Point(0, 100));
        assertEquals(Math.PI, robot.getAngleToTarget(), 0.001);
    }

    @Test
    public void testClampToBounds() {
        robot.updateBounds(800, 600);

        robot.setTarget(new Point(900, 300));
        Point target = robot.getTarget();
        assertEquals(780, target.x);

        robot.setTarget(new Point(-50, 300));
        target = robot.getTarget();
        assertEquals(20, target.x);

        robot.setTarget(new Point(400, 700));
        target = robot.getTarget();
        assertEquals(580, target.y);

        robot.setTarget(new Point(400, -50));
        target = robot.getTarget();
        assertEquals(20, target.y);
    }

    @Test
    public void testSetStopped() {
        robot.setStopped(true);
        assertTrue(robot.isStopped());

        boolean moved = robot.moveOneStep();
        assertFalse(moved);
    }
}