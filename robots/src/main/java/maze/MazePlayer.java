package maze;

import java.awt.Color;
import java.awt.Point;

public class MazePlayer {
    private final int id;
    private Point position;
    private Color color;
    private boolean finished;

    public MazePlayer(int id, Color defaultColor) {
        this.id = id;
        this.color = defaultColor;
        this.finished = false;
    }
    public void setPosition(Point position) { this.position = position; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public int getId() { return id; }
    public Point getPosition() { return position; }
}