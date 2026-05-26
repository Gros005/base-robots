package maze;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MazeCellTest {

    @Test
    public void testEnumValues() {
        MazeCell[] values = MazeCell.values();
        assertEquals(4, values.length);
        assertTrue(containsValue(values, MazeCell.WALL));
        assertTrue(containsValue(values, MazeCell.PATH));
        assertTrue(containsValue(values, MazeCell.START));
        assertTrue(containsValue(values, MazeCell.FINISH));
    }

    private boolean containsValue(MazeCell[] values, MazeCell cell) {
        for (MazeCell value : values) {
            if (value == cell) return true;
        }
        return false;
    }
}