package maze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Point;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

public class MazePlayerTest {

    private MazePlayer player;
    private final Color testColor = Color.RED;

    @BeforeEach
    public void setUp() {
        player = new MazePlayer(1, testColor);
    }

    @Test
    public void testConstructor() {
        assertEquals(1, player.getId());
        assertEquals(testColor, player.getColor());
        assertNull(player.getPosition());
        assertFalse(player.isFinished());
    }

    @Test
    public void testSetAndGetPosition() {
        Point pos = new Point(5, 10);
        player.setPosition(pos);
        assertEquals(5, player.getPosition().x);
        assertEquals(10, player.getPosition().y);
    }

    @Test
    public void testSetAndGetColor() {
        Color newColor = Color.BLUE;
        player.setColor(newColor);
        assertEquals(newColor, player.getColor());
    }

    @Test
    public void testSetAndGetFinished() {
        assertFalse(player.isFinished());
        player.setFinished(true);
        assertTrue(player.isFinished());
        player.setFinished(false);
        assertFalse(player.isFinished());
    }

    @Test
    public void testMultiplePlayersHaveDifferentIds() {
        MazePlayer player1 = new MazePlayer(1, Color.RED);
        MazePlayer player2 = new MazePlayer(2, Color.BLUE);
        assertEquals(1, player1.getId());
        assertEquals(2, player2.getId());
        assertNotEquals(player1.getId(), player2.getId());
    }

    @Test
    public void testPositionValues() {
        Point pos = new Point(7, 12);
        player.setPosition(pos);
        assertEquals(7, player.getPosition().x);
        assertEquals(12, player.getPosition().y);
    }
}