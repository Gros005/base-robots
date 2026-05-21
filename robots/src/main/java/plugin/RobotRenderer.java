package plugin;

import model.Robot;

import java.awt.Graphics2D;

public interface RobotRenderer {
    void paint(Graphics2D graphics, Robot robot);
}
