package gui;

import model.Robot;
import service.RobotMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class GameWindowTest {

    private RobotMovementService service;

    @AfterEach
    public void tearDown() {
        // Останавливаем сервис после каждого теста
        if (service != null) {
            service.shutdown();
        }
    }

    @Test
    public void testGetVisualizer() {
        Robot robot = new Robot();
        service = new RobotMovementService(robot);
        GameVisualizer visualizer = new GameVisualizer(robot, service);
        GameWindow gameWindow = new GameWindow(visualizer);

        // Проверяем, что getVisualizer возвращает тот же объект
        GameVisualizer returnedVisualizer = gameWindow.getVisualizer();
        assertNotNull(returnedVisualizer);
        assertSame(visualizer, returnedVisualizer);
    }

    @Test
    public void testGameWindowCreation() {
        Robot robot = new Robot();
        service = new RobotMovementService(robot);
        GameVisualizer visualizer = new GameVisualizer(robot, service);

        assertDoesNotThrow(() -> {
            GameWindow gameWindow = new GameWindow(visualizer);
            assertNotNull(gameWindow);
            assertEquals("Игровое поле", gameWindow.getTitle());
        });
    }

    @Test
    public void testGameWindowIsResizable() {
        Robot robot = new Robot();
        service = new RobotMovementService(robot);
        GameVisualizer visualizer = new GameVisualizer(robot, service);
        GameWindow gameWindow = new GameWindow(visualizer);

        assertTrue(gameWindow.isResizable());
    }
}