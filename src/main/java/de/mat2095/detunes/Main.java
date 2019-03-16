package de.mat2095.detunes;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;


class Main {

    public static void main(String[] args) throws XInputNotLoadedException, InterruptedException {
        System.out.println("Hello " + args[0]);
        XInputDevice controller = XInputDevice.getDeviceFor(0);
        while (true) {
            controller.poll();
            XInputComponents state = controller.getComponents();
            System.out.println(state.getButtons().b);
            Thread.sleep(50);
        }
    }
}
