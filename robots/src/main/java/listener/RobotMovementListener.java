package listener;

import model.Robot;

/**
 * Интерфейс для получения уведомлений о движении робота.
 */
public interface RobotMovementListener {
    /**
     * Вызывается, когда робот изменил свою позицию
     * @param robot робот, который двигался
     */
    void onRobotMoved(Robot robot);
}