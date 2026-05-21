package plugin;

public class DefaultRobotPlugin implements RobotPlugin {
    @Override
    public String getDisplayName() {
        return "Робот по умолчанию";
    }

    @Override
    public RobotController createController() {
        return new DefaultRobotController();
    }

    @Override
    public RobotRenderer createRenderer() {
        return new DefaultRobotRenderer();
    }
}
