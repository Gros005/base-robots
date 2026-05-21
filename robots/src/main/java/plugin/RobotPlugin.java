package plugin;

public interface RobotPlugin {
    String getDisplayName();

    RobotController createController();

    RobotRenderer createRenderer();
}
