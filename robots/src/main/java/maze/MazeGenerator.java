package maze;

import java.util.Random;

public class MazeGenerator {

    private static final int SIZE = 21;  // размер лабиринта
    private final MazeCell[][] maze;
    private final Random random;

    public MazeGenerator() {
        this.random = new Random();
        this.maze = new MazeCell[SIZE][SIZE];
    }

    public MazeCell[][] generate() {
        // Инициализируем все клетки как стены
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                maze[i][j] = MazeCell.WALL;
            }
        }

        // Начинаем с (1, 1) - Старт
        maze[1][1] = MazeCell.PATH;
        carvePath(1, 1);

        // Устанавливаем старт и финиш
        maze[1][1] = MazeCell.START;
        maze[SIZE/2][SIZE/2] = MazeCell.FINISH;

        return maze;
    }

    private void carvePath(int x, int y) {
        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        shuffleArray(dirs);

        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && nx < SIZE-1 && ny > 0 && ny < SIZE-1 && maze[nx][ny] == MazeCell.WALL) {
                maze[nx][ny] = MazeCell.PATH;
                maze[x + dir[0]/2][y + dir[1]/2] = MazeCell.PATH;
                carvePath(nx, ny);
            }
        }
    }

    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static int getSize() {
        return SIZE;
    }
}