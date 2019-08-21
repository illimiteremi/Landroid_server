import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class ModeAlone {

    private GpioControler gpioControler;
    private Thread thread;
    private boolean isRunning = false;

    private Pin leftEchoPin = RaspiPin.GPIO_05;
    private Pin leftTrigPin = RaspiPin.GPIO_04;
    private PiJavaUltrasonic leftCapteur;

    private Pin rightEchoPin = RaspiPin.GPIO_07;
    private Pin rightTrigPin = RaspiPin.GPIO_06;
    private PiJavaUltrasonic rightCapteur;

    private int leftDistance;
    private int rightDistance;

    Thread leftCapteurThread = new Thread() {
        public void run() {
            while (isRunning) {
                try {
                    leftDistance = leftCapteur.getDistance();
                    Thread.sleep(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("!! leftCapteurThread Error : " + e);
                }
            }
        }
    };

    Thread rightCapteurThread = new Thread() {
        public void run() {
            while (isRunning) {
                try {
                    rightDistance = rightCapteur.getDistance();
                    Thread.sleep(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("!! rightCapteurThread Error : " + e);
                }
            }
        }
    };

    /**
     * Class AloneModeThread
     */
    class AloneModeThread implements Runnable {
        @Override
        public void run() {
            // Start getDistance
            leftCapteurThread.start();
            rightCapteurThread.start();
            float turnRobot = 0; // Center

            while (isRunning) {
                try {
                    Thread.sleep(500);
                    System.out.println("--> Distance : L = " + leftDistance + " / R = " + rightDistance);
                    turnRobot = checkDirection(leftDistance, rightDistance);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("!! AloneModeThread Error : " + e);
                }
            }

            // Stop getDistance
            leftCapteurThread.join();
            rightCapteurThread.join();
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
            leftCapteur = new PiJavaUltrasonic(leftEchoPin.getAddress(), leftTrigPin.getAddress(), 1000, 23529411);
            rightCapteur = new PiJavaUltrasonic(rightEchoPin.getAddress(), rightTrigPin.getAddress(), 1000, 23529411);
        } catch (Exception ex) {
            System.out.println("!! ModeAlone Error : " + ex);
        }
    }

    /**
     * startModeAlone
     */
    public void startModeAlone() {
        System.out.println("--> startModeAlone...");
        isRunning = true;
        thread = new Thread(new AloneModeThread());
        thread.start();
    }

    /**
     * stopModeAlone
     */
    public void stopModeAlone() {
        System.out.println("--> stopModeAlone...");
        try {
            isRunning = false;
            gpioControler.stopAll();
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("!! stopModeAlone Error : " + e);
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
            System.out.println("--> Turn Left <-");
            return -1;
        } else if (leftDist < rightDist) {
            // Turn Right
            System.out.println("--> Turn Right ->");
            return 1;
        } else {
            // Center
            System.out.println("--> Go Center ||");
            return 0;
        }
    }

}
