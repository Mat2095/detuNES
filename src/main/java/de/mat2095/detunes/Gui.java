package de.mat2095.detunes;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


class Gui extends RenderingContext {

    private final BufferStrategy bufferStrategy;
    private final Graphics bufferGraphics;
    private final BufferedImage offscreenImage;
    private final int[] bufferData;
    private final Object bufferLock = new Object();

    Gui(Emulator emu) {
        Frame frame = new Frame("detuNES");
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);

        Canvas canvas = new Canvas() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                synchronized (bufferLock) {
                    bufferStrategy.show();
                    Toolkit.getDefaultToolkit().sync();
                }
            }
        };
        canvas.setPreferredSize(new Dimension(256, 240));
        frame.add(canvas);
        frame.pack();
        canvas.requestFocusInWindow();
        frame.setLocationRelativeTo(null); // center of screen

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                emu.halt();
            }
        });
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        offscreenImage = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        bufferData = ((DataBufferInt) (offscreenImage.getRaster().getDataBuffer())).getData();
        bufferGraphics = bufferStrategy.getDrawGraphics();

        emu.setRenderingContext(this);
    }

    int[] getBufferData() {
        return bufferData;
    }

    void sync() {
        synchronized (bufferLock) {
            bufferGraphics.drawImage(offscreenImage, 0, 0, null);
            bufferStrategy.show();
            Toolkit.getDefaultToolkit().sync();
        }
    }
}
