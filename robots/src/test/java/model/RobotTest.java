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
        // Создаём нового робота для чистоты теста
        Robot testRobot = new Robot();

        // Цель в пределах границ
        Point validTarget = new Point(700, 500);
        testRobot.setTarget(validTarget);

        Point target = testRobot.getTarget();
        assertEquals(700, target.x, "X should be 700");
        assertEquals(500, target.y, "Y should be 500");
        assertFalse(testRobot.isStopped());

        // Цель за пределами границ X
        Point outOfBoundsX = new Point(800, 400);
        testRobot.setTarget(outOfBoundsX);

        Point clampedTargetX = testRobot.getTarget();
        assertEquals(780, clampedTargetX.x, "X should be clamped to 780");
        assertEquals(400, clampedTargetX.y);

        // Цель за пределами границ Y
        Point outOfBoundsY = new Point(500, 600);
        testRobot.setTarget(outOfBoundsY);

        Point clampedTargetY = testRobot.getTarget();
        assertEquals(500, clampedTargetY.x);
        assertEquals(580, clampedTargetY.y, "Y should be clamped to 580");

        // Цель за пределами обеих границ
        Point outOfBoundsBoth = new Point(900, 900);
        testRobot.setTarget(outOfBoundsBoth);

        Point clampedTargetBoth = testRobot.getTarget();
        assertEquals(780, clampedTargetBoth.x, "X should be clamped to 780");
        assertEquals(580, clampedTargetBoth.y, "Y should be clamped to 580");
    }

    @Test
    public void testReset() {
        // Сначала изменим позицию
        robot.setTarget(new Point(500, 500));
        robot.moveOneStep();

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
    public void testMoveOneStep() {
        robot.setTarget(new Point(200, 100));
        double startX = robot.getPositionX();
        double startY = robot.getPositionY();
        boolean moved = false;
        for (int i = 0; i < 5; i++) {
            if (robot.moveOneStep()) {
                moved = true;
            }
        }
        // Проверяем, что позиция изменилась
        assertTrue(moved || (robot.getPositionX() != startX || robot.getPositionY() != startY),
                "Robot should move towards target");
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