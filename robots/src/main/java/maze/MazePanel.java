package maze;

import config.ColorSettings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class MazePanel extends JPanel {

    private static final int CELL_SIZE = 25;
    private static final int ROBOT_WIDTH = 20;
    private static final int ROBOT_HEIGHT = 12;

    // Параметры движения
    private static final double MAX_SPEED = 0.6;
    private static final double ACCELERATION = 0.05;
    private static final double TURN_SPEED = 0.08;
    private static final double FRICTION = 0.98;
    private static final int MAX_TRAIL_SIZE = 200;

    private MazeCell[][] maze;
    private MazePlayer player1;
    private MazePlayer player2;
    private boolean gameActive = true;
    private String winner = null;

    // Состояние игроков
    private final Map<Integer, Boolean> keysPressed = new HashMap<>();
    private double speed1 = 0;
    private double speed2 = 0;
    private double angle1 = 0;
    private double angle2 = 0;

    // Точные координаты
    private double realX1, realY1;
    private double realX2, realY2;

    // Следы
    private final java.util.List<Point> trail1 = new ArrayList<>();
    private final java.util.List<Point> trail2 = new ArrayList<>();

    private javax.swing.Timer gameTimer;

    public MazePanel() {
        setFocusable(true);
        requestFocusInWindow();
        initKeys();
        initGame();
        setupKeyListener();
        startGameLoop();
    }

    private void initKeys() {
        keysPressed.put(KeyEvent.VK_UP, false);
        keysPressed.put(KeyEvent.VK_DOWN, false);
        keysPressed.put(KeyEvent.VK_LEFT, false);
        keysPressed.put(KeyEvent.VK_RIGHT, false);
        keysPressed.put(KeyEvent.VK_W, false);
        keysPressed.put(KeyEvent.VK_S, false);
        keysPressed.put(KeyEvent.VK_A, false);
        keysPressed.put(KeyEvent.VK_D, false);
    }

    private void initGame() {
        MazeGenerator generator = new MazeGenerator();
        maze = generator.generate();

        player1 = new MazePlayer(1, ColorSettings.getInstance().getPlayer1Color());
        player2 = new MazePlayer(2, ColorSettings.getInstance().getPlayer2Color());

        realX1 = 1;
        realY1 = 1;
        realX2 = 1;
        realY2 = 1;

        player1.setPosition(new Point(1, 1));
        player2.setPosition(new Point(1, 1));

        speed1 = 0;
        speed2 = 0;
        angle1 = 0;
        angle2 = 0;

        trail1.clear();
        trail2.clear();
        trail1.add(new Point(1, 1));
        trail2.add(new Point(1, 1));

        gameActive = true;
        winner = null;
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (keysPressed.containsKey(key)) {
                    keysPressed.put(key, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (keysPressed.containsKey(key)) {
                    keysPressed.put(key, false);
                }
            }
        });
    }

    private void startGameLoop() {
        gameTimer = new javax.swing.Timer(30, e -> {
            if (gameActive) {
                updateMovement();
                updateTrails();
                checkWinner();
                repaint();
            }
        });
        gameTimer.start();
    }

    private void updateTrails() {
        Point pos1 = new Point((int) Math.round(realX1), (int) Math.round(realY1));
        Point pos2 = new Point((int) Math.round(realX2), (int) Math.round(realY2));

        if (trail1.isEmpty() || !trail1.getLast().equals(pos1)) {
            trail1.add(pos1);
            while (trail1.size() > MAX_TRAIL_SIZE) trail1.removeFirst();
        }

        if (trail2.isEmpty() || !trail2.getLast().equals(pos2)) {
            trail2.add(pos2);
            while (trail2.size() > MAX_TRAIL_SIZE) trail2.removeFirst();
        }
    }

    private void updateMovement() {
        updatePlayer(1,
                keysPressed.get(KeyEvent.VK_UP),
                keysPressed.get(KeyEvent.VK_DOWN),
                keysPressed.get(KeyEvent.VK_LEFT),
                keysPressed.get(KeyEvent.VK_RIGHT));

        updatePlayer(2,
                keysPressed.get(KeyEvent.VK_W),
                keysPressed.get(KeyEvent.VK_S),
                keysPressed.get(KeyEvent.VK_A),
                keysPressed.get(KeyEvent.VK_D));
    }

    private void updatePlayer(int playerId, boolean up, boolean down, boolean left, boolean right) {
        MazePlayer player = (playerId == 1) ? player1 : player2;
        if (player.isFinished()) return;

        double speed = (playerId == 1) ? speed1 : speed2;
        double angle = (playerId == 1) ? angle1 : angle2;
        double realX = (playerId == 1) ? realX1 : realX2;
        double realY = (playerId == 1) ? realY1 : realY2;

        if (up) {
            speed += ACCELERATION;
            if (speed > MAX_SPEED) speed = MAX_SPEED;
        } else if (down) {
            speed -= ACCELERATION;
            if (speed < -MAX_SPEED/2) speed = -MAX_SPEED/2;
        } else {
            speed *= FRICTION;
            if (Math.abs(speed) < 0.01) speed = 0;
        }

        if (left) angle -= TURN_SPEED;
        if (right) angle += TURN_SPEED;

        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;

        if (Math.abs(speed) > 0.01) {
            double newRealX = realX + Math.sin(angle) * speed;
            double newRealY = realY + Math.cos(angle) * speed;

            int cellX = (int) Math.round(newRealX);
            int cellY = (int) Math.round(newRealY);

            if (isValidMove(cellX, cellY)) {
                realX = newRealX;
                realY = newRealY;
                player.setPosition(new Point(cellX, cellY));

                if (maze[cellX][cellY] == MazeCell.FINISH) {
                    player.setFinished(true);
                }
            } else {
                speed = 0;
            }
        }

        if (playerId == 1) {
            speed1 = speed;
            angle1 = angle;
            realX1 = realX;
            realY1 = realY;
        } else {
            speed2 = speed;
            angle2 = angle;
            realX2 = realX;
            realY2 = realY;
        }
    }

    private boolean isValidMove(int x, int y) {
        if (x < 0 || x >= MazeGenerator.getSize() || y < 0 || y >= MazeGenerator.getSize()) {
            return false;
        }
        MazeCell cell = maze[x][y];
        return cell == MazeCell.PATH || cell == MazeCell.START || cell == MazeCell.FINISH;
    }

    private void checkWinner() {
        if (!gameActive) return;

        if (player1.isFinished() && !player2.isFinished()) {
            gameActive = false;
            winner = "Игрок 1 победил!";
            showWinnerDialog();
        } else if (player2.isFinished() && !player1.isFinished()) {
            gameActive = false;
            winner = "Игрок 2 победил!";
            showWinnerDialog();
        } else if (player1.isFinished() && player2.isFinished()) {
            gameActive = false;
            winner = "Ничья!";
            showWinnerDialog();
        }
    }

    private void showWinnerDialog() {
        gameTimer.stop();
        SwingUtilities.invokeLater(() -> {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    winner + "\nХотите сыграть ещё?",
                    "Игра окончена",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                initGame();
                gameTimer.start();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawMaze(g2d);
        drawTrail(g2d, trail1, player1.getColor());
        drawTrail(g2d, trail2, player2.getColor());
        drawRobot(g2d, player1, angle1, realX1, realY1);
        drawRobot(g2d, player2, angle2, realX2, realY2);
    }

    private void drawTrail(Graphics2D g, java.util.List<Point> trail, Color color) {
        if (trail.size() < 2) return;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        g.setStroke(new BasicStroke(3));

        for (int i = 0; i < trail.size() - 1; i++) {
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);

            int x1 = p1.y * CELL_SIZE + CELL_SIZE/2;
            int y1 = p1.x * CELL_SIZE + CELL_SIZE/2;
            int x2 = p2.y * CELL_SIZE + CELL_SIZE/2;
            int y2 = p2.x * CELL_SIZE + CELL_SIZE/2;

            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawMaze(Graphics2D g) {
        int size = MazeGenerator.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;

                switch (maze[i][j]) {
                    case WALL:
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        break;
                    case PATH:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        break;
                    case START:
                        g.setColor(new Color(200, 200, 255));
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        break;
                    case FINISH:
                        g.setColor(Color.GREEN);
                        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Arial", Font.BOLD, 16));
                        g.drawString("★", x + CELL_SIZE/2 - 6, y + CELL_SIZE/2 + 5);
                        break;
                }

                g.setColor(Color.GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawRobot(Graphics2D g, MazePlayer player, double angle, double realX, double realY) {
        int x = (int) (realY * CELL_SIZE + (double) CELL_SIZE /2);
        int y = (int) (realX * CELL_SIZE + (double) CELL_SIZE /2);

        g.rotate(angle, x, y);

        g.setColor(player.getColor());
        g.fillOval(x - ROBOT_WIDTH/2, y - ROBOT_HEIGHT/2, ROBOT_WIDTH, ROBOT_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawOval(x - ROBOT_WIDTH/2, y - ROBOT_HEIGHT/2, ROBOT_WIDTH, ROBOT_HEIGHT);

        int eyeX = x + ROBOT_WIDTH/4;
        int eyeY = y - ROBOT_HEIGHT/4;
        g.setColor(Color.WHITE);
        g.fillOval(eyeX - 3, eyeY - 3, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(eyeX - 1, eyeY - 1, 2, 2);

        g.rotate(-angle, x, y);
    }

    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
}