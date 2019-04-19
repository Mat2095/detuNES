package de.mat2095.detunes;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


class DebugPpuGui extends JFrame {

    private static final Color COLOR_U = new Color(0xFF, 0x00, 0x00, 0xA0);
    private static final Color COLOR_M = new Color(0x40, 0x80, 0xFF, 0xA0);
    private static final Color COLOR_UM = new Color(0xC0, 0x40, 0xFF, 0xA0);

    DebugPpuGui(Emulator emu) {
        super("detuNES - debug PPU");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel palette = new JPanel(new GridLayout(8, 4));

        ColorLabel[] colorLabels = new ColorLabel[0x20];

        for (int addr = 0x3F00; addr < 0x3F20; addr++) {
            Color color;
            switch (addr) {
                case 0x3F04:
                case 0x3F08:
                case 0x3F0C:
                    color = COLOR_U;
                    break;
                case 0x3F10:
                    color = COLOR_M;
                    break;
                case 0x3F14:
                case 0x3F18:
                case 0x3F1C:
                    color = COLOR_UM;
                    break;
                default:
                    color = null;
            }
            colorLabels[addr - 0x3F00] = new ColorLabel(addr, color);
            palette.add(colorLabels[addr - 0x3F00]);
        }

        add(palette);

        pack();
        setVisible(true);

        new Thread(() -> {
            while (isShowing()) {
                for (int addr = 0x3F00; addr < 0x3F20; addr++) {
                    colorLabels[addr - 0x3F00].setValue(emu.readPpu(addr));
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class ColorLabel extends JPanel {

        JLabel addrLabel;
        JLabel valueLabel;

        private ColorLabel(int addr, Color borderColor) {
            super(new GridLayout(2, 1));
            addrLabel = new JLabel(Util.getHexString16bit(addr));
            add(addrLabel);
            valueLabel = new JLabel();
            add(valueLabel);
            setBorder(
                borderColor != null
                    ? new CompoundBorder(new LineBorder(borderColor, 3), new EmptyBorder(1, 13, 1, 13))
                    : new EmptyBorder(4, 16, 4, 16)
            );
        }

        private void setValue(byte value) {
            valueLabel.setText(Util.getHexString(value));

            int color = Ppu.PALETTE[value];
            setBackground(new Color(color));

            Color contrast = (color & 0xFF) + (color >>> 8 & 0xFF) + (color >>> 16 & 0xFF) > 0x180 ? Color.BLACK : Color.WHITE;
            addrLabel.setForeground(contrast);
            valueLabel.setForeground(contrast);
        }
    }
}
