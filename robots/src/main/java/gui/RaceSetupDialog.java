package gui;

import config.ColorSettings;
import javax.swing.*;
import java.awt.*;

public class RaceSetupDialog {

    public static int showRobotCountDialog(Component parent) {
        int maxRobots = ColorSettings.getMaxRobotsCount();

        String input = JOptionPane.showInputDialog(parent,
                "Введите количество роботов (1–" + maxRobots + "):",
                "Настройка гонки",
                JOptionPane.QUESTION_MESSAGE);
        try {
            int count = Integer.parseInt(input);
            if (count < 1) return 1;
            return Math.min(count, maxRobots);
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    public static PlacementMode showPlacementModeDialog(Component parent) {
        int choice = JOptionPane.showOptionDialog(parent,
                "Как расставить роботов?",
                "Режим расстановки",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Рандомно", "Вручную"},
                "Рандомно");

        return choice == 0 ? PlacementMode.RANDOM : PlacementMode.MANUAL;
    }

    public enum PlacementMode {
        RANDOM, MANUAL
    }
}