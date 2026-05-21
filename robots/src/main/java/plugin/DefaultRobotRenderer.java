package plugin;

import model.Robot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class DefaultRobotRenderer implements RobotRenderer {
    private static final int ROBOT_WIDTH = 30;
    private static final int ROBOT_HEIGHT = 10;
    private static final int ROBOT_EYE_OFFSET = 10;
    private static final int ROBOT_EYE_SIZE = 5;

    @Override
    public void paint(Graphics2D graphics, Robot robot) {
        int x = (int) Math.round(robot.getPositionX());
        int y = (int) Math.round(robot.getPositionY());
        double direction = robot.getDirection();

        AffineTransform oldTransform = graphics.getTransform();
        AffineTransform transform = AffineTransform.getRotateInstance(direction, x, y);
        graphics.setTransform(transform);

        graphics.setColor(Color.MAGENTA);
        fillOval(graphics, x, y, ROBOT_WIDTH, ROBOT_HEIGHT);

        graphics.setColor(Color.BLACK);
        drawOval(graphics, x, y, ROBOT_WIDTH, ROBOT_HEIGHT);

        graphics.setColor(Color.WHITE);
        fillOval(graphics, x + ROBOT_EYE_OFFSET, y, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);

        graphics.setColor(Color.BLACK);
        drawOval(graphics, x + ROBOT_EYE_OFFSET, y, ROBOT_EYE_SIZE, ROBOT_EYE_SIZE);
        graphics.setTransform(oldTransform);
    }

    private void fillOval(Graphics graphics, int x, int y, int width, int height) {
        graphics.fillOval(x - width / 2, y - height / 2, width, height);
    }

    private void drawOval(Graphics graphics, int x, int y, int width, int height) {
        graphics.drawOval(x - width / 2, y - height / 2, width, height);
    }
}
