package race;

import model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RaceManagerTest {

    private List<Robot> robots;
    private RaceManager raceManager;
    private boolean raceFinished = false;

    @BeforeEach
    public void setUp() {
        robots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Robot robot = new Robot(100 + i * 100, 100, 500, 300);
            robots.add(robot);
        }

        raceManager = new RaceManager(robots, () -> raceFinished = true);
    }

    @Test
    public void testStartRace() {
        raceManager.startRace();
        assertTrue(raceManager.isRaceActive());
        assertNull(raceManager.getWinner());
    }

    @Test
    public void testWinnerDetection() {
        raceManager.startRace();

        // Симулируем победу первого робота
        robots.get(0).setStopped(true);
        raceManager.checkWinner();

        assertFalse(raceManager.isRaceActive());
        assertNotNull(raceManager.getWinner());
        assertSame(robots.get(0), raceManager.getWinner());
        assertTrue(raceFinished);
    }

    @Test
    public void testOnlyFirstWinner() {
        raceManager.startRace();

        robots.get(0).setStopped(true);
        robots.get(1).setStopped(true);
        raceManager.checkWinner();

        assertSame(robots.get(0), raceManager.getWinner());
    }

    @Test
    public void testNoWinner() {
        raceManager.startRace();

        raceManager.checkWinner();

        assertTrue(raceManager.isRaceActive());
        assertNull(raceManager.getWinner());
        assertFalse(raceFinished);
    }
}