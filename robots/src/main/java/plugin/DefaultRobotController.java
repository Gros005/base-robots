package plugin;

import model.Robot;

public class DefaultRobotController implements RobotController {
    private static final double FULL_CIRCLE_RADIANS = Math.PI * 2;
    private static final double HALF_CIRCLE_RADIANS = Math.PI;
    private static final double TARGET_REACHED_THRESHOLD = 0.5;

    @Override
    public void update(Robot robot, double durationMs) {
        if (robot.getDistanceToTarget() < TARGET_REACHED_THRESHOLD) {
            return;
        }

        double velocity = robot.getMaxVelocity();
        double angleToTarget = robot.getAngleToTarget();
        double currentDirection = robot.getDirection();
        double angleDifference = normalizeAngleDifference(angleToTarget - currentDirection);

        double angularVelocity = 0;
        if (angleDifference > 0) {
            angularVelocity = robot.getMaxAngularVelocity();
        } else if (angleDifference < 0) {
            angularVelocity = -robot.getMaxAngularVelocity();
        }

        robot.move(velocity, angularVelocity, durationMs);
    }

    private double normalizeAngleDifference(double angleDifference) {
        while (angleDifference <= -HALF_CIRCLE_RADIANS) {
            angleDifference += FULL_CIRCLE_RADIANS;
        }
        while (angleDifference > HALF_CIRCLE_RADIANS) {
            angleDifference -= FULL_CIRCLE_RADIANS;
        }
        return angleDifference;
    }
}
