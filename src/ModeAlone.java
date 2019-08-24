import java.util.concurrent.Semaphore;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class ModeAlone {

    private GpioControler gpioControler;
    private Thread mainThread;
    private Thread leftThread;
    private Thread rightThread;
    private boolean isRunning = false;

    private PiJavaUltrasonic leftCapteur;
    private Pin leftEchoPin = RaspiPin.GPIO_05;
    private Pin leftTrigPin = RaspiPin.GPIO_04;

    private PiJavaUltrasonic rightCapteur;
    private Pin rightEchoPin = RaspiPin.GPIO_10;
    private Pin rightTrigPin = RaspiPin.GPIO_06;

    public int leftDistance;
    public int rightDistance;

    private class CapteurThread implements Runnable {

        private PiJavaUltrasonic piJavaUltrasonic;

        public CapteurThread(PiJavaUltrasonic piJavaUltrasonic) {

            this.piJavaUltrasonic = piJavaUltrasonic;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    int distance = piJavaUltrasonic.getDistance();
                    if (piJavaUltrasonic.name.contains("LEFT")) {
                        leftDistance = distance;
                    }
                    if (piJavaUltrasonic.name.contains("RIGHT")) {
                        rightDistance = distance;
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("--> leftCapteurThread Error : " + e);
                }
            }
        }
    }

    /**
     * Class AloneModeThread
     */
    private class AloneModeThread implements Runnable {
        final Semaphore semaphore;
        private Object locker;

        public AloneModeThread() {

            // Create Semaphore
            semaphore = new Semaphore(2, true);
            // Start getDistance
            leftThread = new Thread(new CapteurThread(leftCapteur));
            rightThread = new Thread(new CapteurThread(rightCapteur));
            leftThread.start();
            rightThread.start();
        }

        @Override
        public void run() {
            while (isRunning) {
                    try {
                        Thread.sleep(500);
                        System.out.println("--> Distance : L = " + leftDistance + " cm / R = " + rightDistance + " cm");
                        float turnRobot = checkDirection(leftDistance, rightDistance);
                        if (leftDistance >= 100) {
                            gpioControler.leftMotor.controlMotor(100, 1);
                        } else if (leftDistance <= 10) {
                            gpioControler.leftMotor.controlMotor(0, 1);
                        } else if (leftDistance <= 20) {
                            gpioControler.leftMotor.controlMotor(20, 1);
                        } else {
                            gpioControler.leftMotor.controlMotor(leftDistance, 1);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                        System.out.println("--> AloneModeThread Error : " + e);
                    }
            }
        }
    }

    /**
     * UserInterface Controler
     */
    public ModeAlone(final GpioControler gpioControler) {
        System.out.println("<-- Landroid Project --> Init Mode Alone");

        // Create gpio controller for motor
        try {
            this.gpioControler = gpioControler;
            leftCapteur = new PiJavaUltrasonic(leftEchoPin.getAddress(), leftTrigPin.getAddress(), 1000, 23529411,
                    "LEFT");
            rightCapteur = new PiJavaUltrasonic(rightEchoPin.getAddress(), rightTrigPin.getAddress(), 1000, 23529411,
                    "RIGHT");
        } catch (Exception ex) {
            System.out.println("--> ModeAlone Error : " + ex);
        }
    }

    /**
     * startModeAlone
     */
    public void startModeAlone() {
        System.out.println("--> startModeAlone...");
        isRunning = true;
        mainThread = new Thread(new AloneModeThread());
        mainThread.start();
    }

    /**
     * stopModeAlone
     */
    public void stopModeAlone() {
        System.out.println("--> stopModeAlone...");
        try {
            isRunning = false;
            mainThread.join();
            leftThread.join();
            rightThread.join();
            gpioControler.stopAll();
        } catch (InterruptedException e) {
            System.out.println("--> stopModeAlone Error : " + e);
        }
    }

    /**
     * checkDirection
     * 
     * @param leftDist
     * @param rightDist
     * @return
     */
    private float checkDirection(int leftDist, int rightDist) {
        if (leftDist > rightDist) {
            // Turn Left
            System.out.println("--> Turn Left  : <-O");
            return -1;
        } else if (leftDist < rightDist) {
            // Turn Right
            System.out.println("--> Turn Right :  O->");
            return 1;
        } else {
            // Center
            System.out.println("--> Go Center  :  |||");
            return 0;
        }
    }
}
