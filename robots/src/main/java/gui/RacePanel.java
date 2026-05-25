package gui;

import config.ColorSettings;
import model.Robot;
import service.RobotMovementService;
import listener.RobotMovementListener;
import log.Logger;
import race.RaceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class RacePanel extends AbstractRobotPanel implements RobotMovementListener {
    private List<Robot> robots;
    private List<RobotMovementService> services;
    private List<Color> colors;
    private RaceManager raceManager;
    private Point commonTarget;
    private boolean raceActive = false;

    private Map<Robot, List<Point>> trails;
    private static final int MAX_TRAIL_SIZE = 200;

    private boolean awaitingManualPlacement = false;
    private int manualRobotsToPlace = 0;

    public RacePanel() {
        super();
        setupMouseListener();
        Logger.debug("RacePanel создан");
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (awaitingManualPlacement) {
                    addRobotAt(e.getPoint());
                } else if (raceActive && commonTarget != null) {
                    commonTarget = e.getPoint();
                    if (robots != null) {
                        for (Robot robot : robots) {
                            robot.setTarget(commonTarget);
                        }
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    protected void updateBounds(int width, int height) {
        if (robots != null) {
            for (Robot robot : robots) {
                robot.updateBounds(width, height);
            }
        }
    }

    public void setupRace(int robotCount, RaceSetupDialog.PlacementMode mode) {
        robots = new ArrayList<>();
        colors = new ArrayList<>();
        services = new ArrayList<>();
        trails = new HashMap<>();
        raceActive = false;
        awaitingManualPlacement = false;

        int width = getWidth() > 0 ? getWidth() : 800;
        int height = getHeight() > 0 ? getHeight() : 600;
        commonTarget = new Point(width / 2, height / 2);

        if (mode == RaceSetupDialog.PlacementMode.RANDOM) {
            int margin = 50;
            for (int i = 0; i < robotCount; i++) {
                int x = margin + (int)(Math.random() * (width - 2 * margin));
                int y = margin + (int)(Math.random() * (height - 2 * margin));
                createAndAddRobot(x, y, ColorSettings.getRobotColorByIndex(i));
            }
            startRace();
        } else {
            awaitingManualPlacement = true;
            manualRobotsToPlace = robotCount;
            JOptionPane.showMessageDialog(this,
                    "Кликайте на поле, чтобы расставить " + robotCount + " роботов",
                    "Ручная расстановка",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        repaint();
    }

    private void createAndAddRobot(int x, int y, Color color) {
        Robot robot = new Robot(x, y, commonTarget.x, commonTarget.y);
        robots.add(robot);
        colors.add(color);
        trails.put(robot, new ArrayList<>());
        trails.get(robot).add(new Point(x, y));
        Logger.debug("Робот создан в (" + x + ", " + y + ")");
    }

    private void addRobotAt(Point pos) {
        if (!awaitingManualPlacement || manualRobotsToPlace <= 0) return;

        Color color = ColorSettings.getRobotColorByIndex(robots.size());
        Robot robot = new Robot(pos.x, pos.y, commonTarget.x, commonTarget.y);
        robots.add(robot);
        colors.add(color);
        trails.put(robot, new ArrayList<>());
        trails.get(robot).add(new Point(pos.x, pos.y));

        manualRobotsToPlace--;
        repaint();

        if (manualRobotsToPlace == 0) {
            awaitingManualPlacement = false;
            int choice = JOptionPane.showConfirmDialog(this,
                    "Все роботы расставлены. Начать гонку?",
                    "Гонка готова",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startRace();
            }
        }
    }

    private void startRace() {
        if (robots == null || robots.isEmpty()) return;

        updateBounds(getWidth(), getHeight());

        services = new ArrayList<>();
        for (Robot robot : robots) {
            RobotMovementService service = new RobotMovementService(robot);
            service.addListener(this);
            robot.setTarget(commonTarget);
            services.add(service);
        }

        raceManager = new RaceManager(robots, () -> {
            Robot winner = raceManager.getWinner();
            if (winner != null) {
                int winnerIndex = robots.indexOf(winner);
                String colorName = ColorSettings.getColorNameByColor(colors.get(winnerIndex));
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(RacePanel.this,
                            "Робот " + colorName + " победил!",
                            "Гонка завершена",
                            JOptionPane.INFORMATION_MESSAGE);
                });
                raceActive = false;
            }
        });

        raceActive = true;
        raceManager.startRace();
        Logger.debug("Гонка начата с " + robots.size() + " роботами");
    }

    @Override
    public void onRobotMoved(Robot robot) {
        if (trails != null && trails.containsKey(robot)) {
            List<Point> trail = trails.get(robot);
            trail.add(new Point((int) robot.getX(), (int) robot.getY()));
            while (trail.size() > MAX_TRAIL_SIZE) trail.removeFirst();
        }
        if (raceManager != null && raceManager.isRaceActive()) {
            raceManager.checkWinner();
        }
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (trails != null && robots != null) {
            for (int i = 0; i < robots.size(); i++) {
                drawTrail(g2d, trails.get(robots.get(i)), colors.get(i));
            }
        }
        drawTarget(g2d, commonTarget);
        if (robots != null) {
            for (int i = 0; i < robots.size(); i++) {
                drawRobot(g2d, robots.get(i), colors.get(i));
            }
        }
    }

    public void stopRace() {
        raceActive = false;
        if (services != null) {
            for (RobotMovementService s : services) {
                if (s != null) s.shutdown();
            }
        }
    }

    public void shutdown() {
        stopRace();
    }
}