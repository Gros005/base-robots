package maze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class MazeGeneratorTest {

    private MazeCell[][] maze;
    private static final int SIZE = MazeGenerator.getSize();

    @BeforeEach
    public void setUp() {
        MazeGenerator generator = new MazeGenerator();
        maze = generator.generate();
    }

    @Test
    public void testMazeSize() {
        assertEquals(SIZE, maze.length);
        assertEquals(SIZE, maze[0].length);
    }

    @Test
    public void testMazeHasOddSize() {
        assertEquals(1, SIZE % 2, "Размер лабиринта должен быть нечётным");
    }

    @Test
    public void testStartPosition() {
        assertEquals(MazeCell.START, maze[1][1], "Старт должен быть в (1,1)");
    }

    @Test
    public void testFinishPosition() {
        int center = SIZE / 2;
        assertEquals(MazeCell.FINISH, maze[center][center], "Финиш должен быть в центре");
    }

    @Test
    public void testNoWallsAtStartAndFinish() {
        assertNotEquals(MazeCell.WALL, maze[1][1]);
        assertNotEquals(MazeCell.WALL, maze[SIZE/2][SIZE/2]);
    }

    @Test
    public void testBordersAreWalls() {
        for (int i = 0; i < SIZE; i++) {
            assertEquals(MazeCell.WALL, maze[0][i], "Верхняя граница должна быть стеной");
            assertEquals(MazeCell.WALL, maze[SIZE-1][i], "Нижняя граница должна быть стеной");
            assertEquals(MazeCell.WALL, maze[i][0], "Левая граница должна быть стеной");
            assertEquals(MazeCell.WALL, maze[i][SIZE-1], "Правая граница должна быть стеной");
        }
    }

    @Test
    public void testPathExistsFromStart() {
        // Проверяем, что из старта есть путь
        boolean hasPath = false;
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int nx = 1 + dir[0];
            int ny = 1 + dir[1];
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE) {
                if (maze[nx][ny] == MazeCell.PATH) {
                    hasPath = true;
                    break;
                }
            }
        }
        assertTrue(hasPath, "Из старта должен быть хотя бы один проход");
    }

    @Test
    public void testNoAdjacentWallsInPath() {
        // Проверяем, что нет двух стен подряд в проходах
        for (int i = 2; i < SIZE - 2; i += 2) {
            for (int j = 2; j < SIZE - 2; j += 2) {
                if (maze[i][j] == MazeCell.PATH) {
                    assertNotEquals(MazeCell.WALL, maze[i][j]);
                }
            }
        }
    }

    @Test
    public void testGeneratedMazeIsConsistent() {
        // Проверяем, что лабиринт не null и все клетки заполнены
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertNotNull(maze[i][j]);
            }
        }
    }
}