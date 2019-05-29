package de.mat2095.detunes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.stream.Collectors;


class InputConfigGui extends JFrame {

    private final InputProviderImpl ip;

    InputConfigGui(InputProviderImpl ip) throws HeadlessException {
        super("detuNES - configure input");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        this.ip = ip;

        for (int player = 0; player < 2; player++) {
            JLabel playerLabel = new JLabel("player " + (player + 1));
            add(playerLabel,
                new GridBagConstraints(1 + player, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        }

        InputProvider.Button[] buttons = InputProvider.Button.values();
        for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
            JLabel typeLabel = new JLabel(buttons[buttonIndex].name().substring(7));
            add(typeLabel,
                new GridBagConstraints(0, 1 + buttonIndex, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        }

        for (int player = 0; player < 2; player++) {
            for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
                InputProviderImpl.InputConditions inputConditions = ip.getInputConditions(player, buttons[buttonIndex]);
                String inputConditionsString = "";
                inputConditionsString += inputConditions.keyboardCodes.stream()
                    .sorted()
                    .map(KeyEvent::getKeyText)
                    .collect(Collectors.joining(", "));
                JLabel conditionsLabel = new JLabel(inputConditionsString);
                add(conditionsLabel,
                    new GridBagConstraints(1 + player, 1 + buttonIndex, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
            }
        }

        pack();
        setVisible(true);
    }
}
