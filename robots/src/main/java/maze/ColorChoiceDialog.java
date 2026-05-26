package maze;

import config.ColorSettings.ColorPreset;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ColorChoiceDialog {

    private static final List<ColorPreset> AVAILABLE_PRESETS = Arrays.asList(
            ColorPreset.RED, ColorPreset.BLUE, ColorPreset.GREEN, ColorPreset.YELLOW,
            ColorPreset.ORANGE, ColorPreset.CYAN, ColorPreset.PINK, ColorPreset.PURPLE
    );

    private static final List<String> COLOR_NAMES = Arrays.asList(
            "Красный", "Синий", "Зелёный", "Жёлтый",
            "Оранжевый", "Голубой", "Розовый", "Фиолетовый"
    );

    private ColorPreset player1Preset;
    private ColorPreset player2Preset;
    private boolean confirmed = false;

    public ColorChoiceDialog(Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Выбор цвета игроков", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // игрок 1
        JPanel player1Panel = new JPanel(new BorderLayout(10, 10));
        player1Panel.setBorder(BorderFactory.createTitledBorder("Игрок 1 (стрелки)"));

        JComboBox<String> player1Combo = new JComboBox<>(COLOR_NAMES.toArray(new String[0]));
        player1Panel.add(player1Combo, BorderLayout.NORTH);

        JPanel preview1 = new JPanel();
        preview1.setPreferredSize(new Dimension(100, 100));
        preview1.setBackground(AVAILABLE_PRESETS.getFirst().color);
        player1Panel.add(preview1, BorderLayout.CENTER);

        // игрок 2
        JPanel player2Panel = new JPanel(new BorderLayout(10, 10));
        player2Panel.setBorder(BorderFactory.createTitledBorder("Игрок 2 (WASD)"));

        JComboBox<String> player2Combo = new JComboBox<>(COLOR_NAMES.toArray(new String[0]));
        player2Combo.setSelectedIndex(1);
        player2Panel.add(player2Combo, BorderLayout.NORTH);

        JPanel preview2 = new JPanel();
        preview2.setPreferredSize(new Dimension(100, 100));
        preview2.setBackground(AVAILABLE_PRESETS.get(1).color);
        player2Panel.add(preview2, BorderLayout.CENTER);

        centerPanel.add(player1Panel);
        centerPanel.add(player2Panel);

        player1Combo.addActionListener(e -> {
            int idx = player1Combo.getSelectedIndex();
            preview1.setBackground(AVAILABLE_PRESETS.get(idx).color);
        });

        player2Combo.addActionListener(e -> {
            int idx = player2Combo.getSelectedIndex();
            preview2.setBackground(AVAILABLE_PRESETS.get(idx).color);
        });

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Начать игру");
        JButton cancelButton = new JButton("Отмена");

        okButton.addActionListener(e -> {
            int idx1 = player1Combo.getSelectedIndex();
            int idx2 = player2Combo.getSelectedIndex();

            if (idx1 == idx2) {
                JOptionPane.showMessageDialog(dialog,
                        "Цвета игроков не могут совпадать!\nВыберите разные цвета.",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            player1Preset = AVAILABLE_PRESETS.get(idx1);
            player2Preset = AVAILABLE_PRESETS.get(idx2);
            confirmed = true;
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    public boolean isConfirmed() {return confirmed;}

    public ColorPreset getPlayer1Preset() {return player1Preset;}

    public ColorPreset getPlayer2Preset() {return player2Preset;}
}