import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class ModeAlone {

    private final Console console;
    private final GpioControler gpioControler;
    private final Thread thread;

    /*
     * private final Pin leftEchoPin = RaspiPin.GPIO_02; private final Pin
     * leftTrigPin = RaspiPin.GPIO_02;
     * 
     * private final Pin rightEchoPin = RaspiPin.GPIO_02; private final Pin
     * rightTrigPin = RaspiPin.GPIO_02;
     * 
     * private final PiJavaUltrasonic leftCapteur; private final PiJavaUltrasonic
     * rightCapteur;
     */

    class AloneModeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                console.println("is running...");
                gpioControler.leftMotor.controlMotor(20, 1);
                Thread.sleep(1000);
                gpioControler.leftMotor.controlMotor(20, 0);
                Thread.sleep(1000);
            }
        }
    }

    /**
     * UserInterface Controler
     */
    public ModeAlone(GpioControler gpioControler) {
        console = new Console();
        console.title("<-- Landroid Project -->", "Init Mode Alone");

        // Create gpio controller for motor
        try {
            this.gpioControler = gpioControler;
            // leftCapteur = new PiJavaUltrasonic(leftEchoPin.getAddress(),
            // leftTrigPin.getAddress());
            // rightCapteur = new PiJavaUltrasonic(rightEchoPin.getAddress(),
            // rightTrigPin.getAddress());

            // init AloneModeThread
            thread = new Thread(new AloneModeThread());
        } catch (Exception ex) {
            console.err.println("Motor Error : " + ex);
        }
    }

    public void startModeAlone() {
        console.println("startModeAlone...");
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void stopModeAlone() {
        console.println("stopModeAlone...");
        if (thread.isAlive()) {
            thread.interrupt();
        }
        gpioControler.stopAll();
    }

}
