package de.mat2095.detunes;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import javax.swing.*;


class Gui extends RenderingContext {

    private static final int INTERNAL_WIDTH = 256, INTERNAL_HEIGHT = 240;

    private final JFrame frame;
    private final JPanel canvas;
    private final ArrayList<JRadioButtonMenuItem> videoSizeMenuItems;
    private final BufferedImage offscreenImage;
    private final int[] bufferData;
    private DebugPpuGui debugPpuGui;

    Gui(Emulator emu) {
        offscreenImage = new BufferedImage(INTERNAL_WIDTH, INTERNAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferData = ((DataBufferInt) (offscreenImage.getRaster().getDataBuffer())).getData();

        frame = new JFrame("detuNES");

        JMenuBar menuBar = new JMenuBar();
        JMenu videoSizeMenu = new JMenu("Video Size");
        videoSizeMenuItems = new ArrayList<>();
        videoSizeMenu.add(generateVideoSizeMenuItem(1));
        menuBar.add(videoSizeMenu);
        JMenu debugMenu = new JMenu("Debug");
        JCheckBoxMenuItem ppuDebugMenuItem = new JCheckBoxMenuItem("PPU");
        ppuDebugMenuItem.addItemListener(e -> {
            if (ppuDebugMenuItem.getState()) {
                if (debugPpuGui == null) {
                    debugPpuGui = new DebugPpuGui(emu);
                    debugPpuGui.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            ppuDebugMenuItem.setState(false);
                            debugPpuGui = null;
                        }
                    });
                }
            } else {
                if (debugPpuGui != null) {
                    debugPpuGui.dispose();
                    debugPpuGui = null;
                }
            }
        });
        debugMenu.add(ppuDebugMenuItem);
        menuBar.add(debugMenu);
        frame.setJMenuBar(menuBar);

        canvas = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                int x = 0;
                int y = 0;
                if (w * INTERNAL_HEIGHT > h * INTERNAL_WIDTH) {
                    x = (w - (h * INTERNAL_WIDTH / INTERNAL_HEIGHT)) / 2;
                    w = h * INTERNAL_WIDTH / INTERNAL_HEIGHT;
                } else if (w * INTERNAL_HEIGHT < h * INTERNAL_WIDTH) {
                    y = (h - (w * INTERNAL_HEIGHT / INTERNAL_WIDTH)) / 2;
                    h = w * INTERNAL_HEIGHT / INTERNAL_WIDTH;
                }
                synchronized (offscreenImage) {
                    g.drawImage(offscreenImage, x, y, w, h, null);
                }
            }

        };
        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateVideoSizeMenuItems();
            }
        });
        canvas.setBackground(Color.DARK_GRAY);
        canvas.setFocusable(true);
        frame.add(canvas);

        // finding possible and initial video-sizes
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        for (int scaling = 2; INTERNAL_WIDTH * scaling <= screenSize.width && INTERNAL_HEIGHT * scaling <= screenSize.height; scaling++) {
            Dimension dim = new Dimension(scaling * INTERNAL_WIDTH, scaling * INTERNAL_HEIGHT);
            canvas.setPreferredSize(dim);
            frame.pack();
            if (!canvas.getSize().equals(dim)) {
                break;
            }
            videoSizeMenu.add(generateVideoSizeMenuItem(scaling));
        }

        canvas.setPreferredSize(new Dimension(0, 0)); // reset, to prevent calling pack() without change (which leads to errors)
        frame.pack();
        int initialScaling = (videoSizeMenuItems.size() * 2 + 2) / 3; // 2/3 rounded up
        canvas.setPreferredSize(new Dimension(INTERNAL_WIDTH * initialScaling, INTERNAL_HEIGHT * initialScaling));
        frame.pack();
        frame.setLocationRelativeTo(null); // center of screen

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (debugPpuGui != null) {
                    debugPpuGui.dispose();
                }
                frame.dispose();
                emu.halt();
            }
        });
        frame.setVisible(true);

        emu.setRenderingContext(this);
    }

    private JRadioButtonMenuItem generateVideoSizeMenuItem(int scaling) {
        JRadioButtonMenuItem videoSizeMenuItem = new JRadioButtonMenuItem(new AbstractAction(scaling + "x") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension newDim = new Dimension(scaling * INTERNAL_WIDTH, scaling * INTERNAL_HEIGHT);
                if (!canvas.getPreferredSize().equals(newDim)) {
                    canvas.setPreferredSize(newDim);
                    frame.pack();
                } else {
                    updateVideoSizeMenuItems();
                }
            }
        });
        videoSizeMenuItems.add(videoSizeMenuItem);
        Dimension preferredSize = videoSizeMenuItem.getPreferredSize();
        videoSizeMenuItem.setPreferredSize(new Dimension(preferredSize.width * 2, preferredSize.height));
        return videoSizeMenuItem;
    }

    private void updateVideoSizeMenuItems() {
        for (int i = 0; i < videoSizeMenuItems.size(); i++) {
            videoSizeMenuItems.get(i)
                .setSelected((i + 1) * INTERNAL_WIDTH == canvas.getWidth() && (i + 1) * INTERNAL_HEIGHT == canvas.getHeight());
        }
    }

    void setBufferData(int addr, int value) {
        synchronized (offscreenImage) {
            bufferData[addr] = value;
        }
    }

    void sync() {
        canvas.repaint();
    }
}
