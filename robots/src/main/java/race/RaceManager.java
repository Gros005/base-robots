package race;

import model.Robot;
import java.util.List;

public class RaceManager {
    private boolean raceActive;
    private Robot winner;
    private final List<Robot> robots;
    private final Runnable onRaceFinished;

    public RaceManager(List<Robot> robots, Runnable onRaceFinished) {
        this.robots = robots;
        this.onRaceFinished = onRaceFinished;
        this.raceActive = false;
        this.winner = null;
    }

    public void startRace() {
        raceActive = true;
        winner = null;
        for (Robot robot : robots) {
            robot.setStopped(false);
        }
    }

    public void checkWinner() {
        if (!raceActive) return;

        for (Robot robot : robots) {
            if (robot.isStopped() && winner == null) {
                winner = robot;
                raceActive = false;
                if (onRaceFinished != null) {
                    onRaceFinished.run();
                }
                break;
            }
        }
    }

    public boolean isRaceActive() { return raceActive; }
    public Robot getWinner() { return winner; }
}