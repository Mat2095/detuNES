package de.mat2095.detunes;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;


class Gui implements RenderingContext {

    private static final int INTERNAL_WIDTH = 256, INTERNAL_HEIGHT = 240;

    private final JFrame frame;
    private final JPanel canvas;
    private final ArrayList<JRadioButtonMenuItem> videoSizeMenuItems;
    private final BufferedImage offscreenImage1, offscreenImage2;
    private final int[] bufferData1, bufferData2;
    private volatile boolean secondBufferWIP;
    private volatile boolean redrawNecessary;
    private final Object bufferLockRead, bufferLockWrite;
    private DebugPpuGui debugPpuGui;
    private InputConfigGui inputConfigGui;

    Gui(Emulator emu) {
        InputProviderImpl ipk = new InputProviderImpl();

        offscreenImage1 = new BufferedImage(INTERNAL_WIDTH, INTERNAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferData1 = ((DataBufferInt) (offscreenImage1.getRaster().getDataBuffer())).getData();
        offscreenImage2 = new BufferedImage(INTERNAL_WIDTH, INTERNAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferData2 = ((DataBufferInt) (offscreenImage2.getRaster().getDataBuffer())).getData();
        secondBufferWIP = false;
        bufferLockRead = new Object();
        bufferLockWrite = new Object();

        frame = new JFrame("detuNES");

        JMenuBar menuBar = new JMenuBar();
        JMenu videoSizeMenu = new JMenu("Video Size");
        videoSizeMenuItems = new ArrayList<>();
        videoSizeMenu.add(generateVideoSizeMenuItem(1));
        menuBar.add(videoSizeMenu);

        JMenu configMenu = new JMenu("Configuration");
        JMenuItem inputConfigMenuItem = new JMenuItem("Input...");
        inputConfigMenuItem.addActionListener(e -> {
            if (inputConfigGui == null) {
                inputConfigGui = new InputConfigGui(frame, ipk);
                inputConfigGui.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        inputConfigGui = null;
                    }
                });
            }
        });
        configMenu.add(inputConfigMenuItem);
        menuBar.add(configMenu);

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
                synchronized (bufferLockRead) {
                    g.drawImage(secondBufferWIP ? offscreenImage1 : offscreenImage2, x, y, w, h, null);
                }
            }

        };

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                ipk.keyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ipk.keyReleased(e.getKeyCode());
            }
        });
        canvas.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ipk.focusLost();
            }
        });
        emu.setInputProvider(ipk);

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

        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int refreshRate = localGraphicsEnvironment.getDefaultScreenDevice().getDisplayMode().getRefreshRate();
        long repaintDelay = 1000000000 / (refreshRate != DisplayMode.REFRESH_RATE_UNKNOWN ? refreshRate : 60);
        ScheduledExecutorService repaintThread = Executors.newSingleThreadScheduledExecutor();
        repaintThread.scheduleAtFixedRate(() -> {
            if (redrawNecessary) {
                redrawNecessary = false;
                canvas.repaint();
            }
        }, 1, repaintDelay, TimeUnit.NANOSECONDS);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                repaintThread.shutdown();
                if (debugPpuGui != null) {
                    debugPpuGui.dispose();
                }
                if (inputConfigGui != null) {
                    inputConfigGui.dispose();
                }
                frame.dispose();
                emu.halt();
                try {
                    repaintThread.awaitTermination(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
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
                canvas.setPreferredSize(newDim);
                frame.pack();
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

    @Override
    public void setBufferData(int addr, int value) {
        synchronized (bufferLockWrite) {
            (secondBufferWIP ? bufferData2 : bufferData1)[addr] = value;
        }
    }

    @Override
    public void sync() {
        synchronized (bufferLockRead) {
            synchronized (bufferLockWrite) {
                secondBufferWIP = !secondBufferWIP;
            }
        }
        redrawNecessary = true;
    }
}
