package de.mat2095.detunes;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


class DebugPpuGui extends JFrame {

    private static final Color COLOR_U = new Color(0xFF, 0x00, 0x00, 0xA0);
    private static final Color COLOR_M = new Color(0x40, 0x80, 0xFF, 0xA0);
    private static final Color COLOR_UM = new Color(0xC0, 0x40, 0xFF, 0xA0);

    private static final String[] PALETTES = new String[]{
        "Background 0",
        "Background 1",
        "Background 2",
        "Background 3",
        "Sprite 0",
        "Sprite 1",
        "Sprite 2",
        "Sprite 3"
    };

    private final Emulator emu;
    private final JPanel chrCanvas;
    private final BufferedImage chrOffscreenImage;
    private final int[] chrOffscreenImageData;
    private final JComboBox<String> paletteChr;
    private final JCheckBox bigSpritesChr;
    private final JPanel nametableCanvas;
    private final BufferedImage nametableOffscreenImage;
    private final int[] nametableOffscreenImageData;


    DebugPpuGui(Emulator emu) {
        super("detuNES - debug PPU");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        this.emu = emu;

        JPanel palette = new JPanel(new GridLayout(8, 4));
        palette.setBorder(new CompoundBorder(new TitledBorder("palettes"), new EmptyBorder(7, 7, 7, 7)));
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
        add(palette,
            new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        JPanel chr = new JPanel(new GridBagLayout());
        chr.setBorder(new TitledBorder("CHR"));
        chrOffscreenImage = new BufferedImage(16 * 8, 32 * 8, BufferedImage.TYPE_INT_RGB);
        chrOffscreenImageData = ((DataBufferInt) (chrOffscreenImage.getRaster().getDataBuffer())).getData();
        chrCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (chrOffscreenImage) {
                    g.drawImage(chrOffscreenImage, 0, 0, 16 * 8 * 2, 32 * 8 * 2, null);
                }
            }
        };
        chrCanvas.setPreferredSize(new Dimension(16 * 8 * 2, 32 * 8 * 2));
        chr.add(chrCanvas,
            new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        paletteChr = new JComboBox<>(PALETTES);
        chr.add(paletteChr,
            new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        bigSpritesChr = new JCheckBox("double height");
        chr.add(bigSpritesChr,
            new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(chr,
            new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        JPanel nametable = new JPanel();
        nametable.setBorder(new TitledBorder("nametable"));
        nametableOffscreenImage = new BufferedImage(512, 480, BufferedImage.TYPE_INT_RGB);
        nametableOffscreenImageData = ((DataBufferInt) (nametableOffscreenImage.getRaster().getDataBuffer())).getData();
        nametableCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (nametableOffscreenImage) {
                    g.drawImage(nametableOffscreenImage, 0, 0, null);
                }
            }
        };
        nametableCanvas.setPreferredSize(new Dimension(512, 480));
        nametable.add(nametableCanvas);
        add(nametable,
            new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        pack();
        setResizable(false);
        setLocation(5, 5);
        setVisible(true);

        new Thread(() -> {
            while (isShowing()) {
                for (int addr = 0x3F00; addr < 0x3F20; addr++) {
                    colorLabels[addr - 0x3F00].setValue(this.emu.readPpu(addr));
                }

                updateChrOffscreenImage();
                chrCanvas.repaint();

                updateNametableOffscreenImage();
                nametableCanvas.repaint();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateChrOffscreenImage() {
        boolean bigSprites = bigSpritesChr.isSelected();
        int selectedPalette = paletteChr.getSelectedIndex();

        synchronized (chrOffscreenImage) {
            for (int tile = 0; tile < 0x0200; tile++) {
                int tileAddr = tile << 4;

                int tileX, tileY;
                if (bigSprites) {
                    tileX = tile >>> 1 & 0x0F;
                    tileY = (tile & 0x01) | (tile >>> 4 & 0x1E);
                } else {
                    tileX = tile & 0x0F;
                    tileY = tile >>> 4;
                }

                renderTile(chrOffscreenImageData, tileAddr, selectedPalette, tileY * 8, tileX * 8, 8 * 16);
            }
        }
    }

    private void updateNametableOffscreenImage() {
        synchronized (nametableOffscreenImage) {

            for (int tableY = 0; tableY < 2; tableY++) {
                for (int tableX = 0; tableX < 2; tableX++) {

                    for (int tileY = 0; tileY < 30; tileY++) {
                        for (int tileX = 0; tileX < 32; tileX++) {

                            int tile = emu.readPpu(0x2000 | (0x0800 * tableY) | (0x0400 * tableX) | (tileY * 32) | tileX) & 0xFF;
                            int tileAddr = tile << 4;
                            if ((emu.getPpu().getRegCtrl() & 0b00010000) != 0) {
                                tileAddr |= 0x1000;
                            }

                            int attrAddr = 0x2000 | (0x0800 * tableY) | (0x0400 * tableX) | 0x03C0 | (0x0008 * (tileY >>> 2)) | (tileX >>> 2);
                            int attrData = emu.readPpu(attrAddr) & 0xFF;
                            int selectedPalette = (attrData >>> ((tileY & 0x02) + (tileX >>> 1 & 0x01)) * 2) & 0x03;

                            renderTile(nametableOffscreenImageData, tileAddr, selectedPalette, tableY * 240 + tileY * 8, tableX * 256 + tileX * 8, 512);
                        }
                    }
                }
            }
        }
    }

    private void renderTile(int[] imageData, int tileAddr, int selectedPalette, int yStart, int xStart, int imageWidth) {
        for (int y = 0; y < 8; y++) {
            int valueLowBits = emu.readPpu(tileAddr + y) & 0xFF;
            int valueHighBits = emu.readPpu(tileAddr + y + 8) & 0xFF;
            for (int x = 0; x < 8; x++) {
                int value = (valueLowBits & (0x80 >>> x)) >>> (7 - x)
                    | Util.shiftRightNegArg(valueHighBits & (0x80 >>> x), 6 - x);
                int paletteIndex = value == 0 ? 0 : (selectedPalette << 2 | value);
                int nesColor = emu.readPpu(0x3F00 | paletteIndex) & 0xFF;
                imageData[(yStart + y) * imageWidth + (xStart + x)] = Ppu.PALETTE[nesColor];
            }
        }
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
