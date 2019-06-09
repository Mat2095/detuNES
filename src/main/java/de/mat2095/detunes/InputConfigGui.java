package de.mat2095.detunes;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


class InputConfigGui extends JDialog {

    private final InputProviderImpl ip;
    private final InputProvider.Button[] buttons;
    private final JButton[][] conditionsButtons;

    InputConfigGui(JFrame owner, InputProviderImpl ip) throws HeadlessException {
        super(owner, "detuNES - configure input");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getRootPane().setBorder(new EmptyBorder(4, 4, 2, 2));

        this.ip = ip;

        for (int player = 0; player < 2; player++) {
            JLabel playerLabel = new JLabel("player " + (player + 1));
            add(playerLabel,
                new GridBagConstraints(1 + player, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        }

        buttons = InputProvider.Button.values();
        for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
            JLabel typeLabel = new JLabel(buttons[buttonIndex].name().substring(7));
            add(typeLabel,
                new GridBagConstraints(0, 1 + buttonIndex, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        }

        conditionsButtons = new JButton[2][buttons.length];
        for (int player = 0; player < 2; player++) {
            for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                JButton conditionsButton = new JButton();
                conditionsButton.setLayout(new BoxLayout(conditionsButton, BoxLayout.Y_AXIS));
                conditionsButton.addActionListener(actionEvent -> {
                    // TODO: open dialog
                });
                add(conditionsButton,
                    new GridBagConstraints(1 + player, 1 + buttonIndex, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
                conditionsButtons[player][buttonIndex] = conditionsButton;
            }
        }

        updateTexts();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
        setVisible(true);
    }

    private void updateTexts() {
        for (int player = 0; player < 2; player++) {
            for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                InputProviderImpl.InputConditions inputConditions = ip.getInputConditions(player, buttons[buttonIndex]);

                JButton conditionsButton = conditionsButtons[player][buttonIndex];
                conditionsButton.removeAll();

                inputConditions.keyboardCodes.stream()
                    .sorted()
                    .map(keyCode -> "Keyboard: " + KeyEvent.getKeyText(keyCode))
                    .forEach(s -> conditionsButton.add(new JLabel(s)));
                inputConditions.controllerInputConditions.stream()
                    .sorted()
                    .map(controllerInputCondition -> "Controller " + controllerInputCondition.player + ": " + controllerInputCondition.type)
                    .forEach(s -> conditionsButton.add(new JLabel(s)));
                if (conditionsButton.getComponentCount() == 0) {
                    conditionsButton.add(new JLabel("[NONE]"));
                }

                conditionsButton.setPreferredSize(new Dimension(256, conditionsButton.getPreferredSize().height));
            }
        }
    }
}
