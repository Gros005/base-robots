package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

public class RobotTest {

    private Robot robot;

    @BeforeEach
    public void setUp() {
        robot = new Robot();
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(100.0, robot.getPositionX(), 0.001);
        assertEquals(100.0, robot.getPositionY(), 0.001);
        assertEquals(0.0, robot.getDirection(), 0.001);

        Point target = robot.getTarget();
        assertEquals(150, target.x);
        assertEquals(100, target.y);
    }

    @Test
    public void testCustomConstructor() {
        Robot customRobot = new Robot(200, 300, 400, 500);

        assertEquals(200.0, customRobot.getPositionX(), 0.001);
        assertEquals(300.0, customRobot.getPositionY(), 0.001);
        assertEquals(0.0, customRobot.getDirection(), 0.001);

        Point target = customRobot.getTarget();
        assertEquals(400, target.x);
        assertEquals(500, target.y);
    }

    @Test
    public void testSetTarget() {
        Point newTarget = new Point(800, 600);
        robot.setTarget(newTarget);

        Point target = robot.getTarget();
        assertEquals(800, target.x);
        assertEquals(600, target.y);

        // Проверяем иммутабельность
        newTarget.x = 999;
        target = robot.getTarget();
        assertEquals(800, target.x);
    }

    @Test
    public void testReset() {
        // Сначала изменим позицию
        robot.setTarget(new Point(500, 500));
        robot.move(0.1, 0.001, 100);

        // Запоминаем, что позиция изменилась
        assertNotEquals(100.0, robot.getPositionX());

        // Сбрасываем
        robot.reset();

        assertEquals(100.0, robot.getPositionX(), 0.001);
        assertEquals(100.0, robot.getPositionY(), 0.001);
        assertEquals(0.0, robot.getDirection(), 0.001);

        Point target = robot.getTarget();
        assertEquals(150, target.x);
        assertEquals(100, target.y);
    }

    @Test
    public void testMove() {
        double oldX = robot.getPositionX();
        double oldY = robot.getPositionY();
        double oldDir = robot.getDirection();

        robot.move(0.1, 0.001, 10);

        assertNotEquals(oldX, robot.getPositionX());
        assertNotEquals(oldY, robot.getPositionY());
        assertNotEquals(oldDir, robot.getDirection());
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
    public void testGetters() {
        assertTrue(robot.getMaxVelocity() > 0);
        assertTrue(robot.getMaxAngularVelocity() > 0);
    }
}