import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class ModeAlone {

    private final Console console;
    private GpioControler gpioControler;
    private Thread thread;
    private boolean isRunning = false;

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
            while(isRunning) {
                try {
                    gpioControler.leftMotor.controlMotor(50, 1);
                    Thread.sleep(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    console.println("AloneModeThread Error : " + e);
                }
            }
        }
    }

    /**
     * UserInterface Controler
     */
    public ModeAlone(final GpioControler gpioControler) {
        console = new Console();
        console.println("<-- Landroid Project -->", "Init Mode Alone");

        // Create gpio controller for motor
        try {
            this.gpioControler = gpioControler;
            // leftCapteur = new PiJavaUltrasonic(leftEchoPin.getAddress(),
            // leftTrigPin.getAddress());
            // rightCapteur = new PiJavaUltrasonic(rightEchoPin.getAddress(),
            // rightTrigPin.getAddress());
        } catch (Exception ex) {
            console.println("ModeAlone Error : " + ex);
        }
    }

    public void startModeAlone() {
        console.println("startModeAlone...");
        isRunning = true;
        thread = new Thread(new AloneModeThread());
        thread.start();
    }

    public void stopModeAlone() {
        console.println("stopModeAlone...");
        try {
            isRunning = false;
            gpioControler.stopAll();
            thread.join();
        } catch (InterruptedException e) {
            console.println("stopModeAlone Error : " + e);
        }
    }

}
