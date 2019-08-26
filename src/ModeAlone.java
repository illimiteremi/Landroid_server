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

    /**
     * CapteurThread
     */
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
                    Thread.sleep(50);
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
        public AloneModeThread() {
            // Start getDistance
            leftThread = new Thread(new CapteurThread(leftCapteur));
            rightThread = new Thread(new CapteurThread(rightCapteur));
            leftThread.start();
            rightThread.start();
        }

        @Override
        public void run() {
            // Init before to start
            int leftMotorSpeed = checkMotorSpeed(leftDistance, gpioControler.leftMotor);
              while (isRunning) {
                try {
                    Thread.sleep(500);
                    // Check type of obstacle
                    Constants.OBSTACLE_TYPE typeObstacle = checkObstacle(leftDistance, rightDistance);
                    switch (typeObstacle) {
                        case WALL:
                            if (leftMotorSpeed == 0) {
                                // reverse leftMotor
                                gpioControler.leftMotor.controlMotor(25,1);
                            } else {
                                leftMotorSpeed = checkMotorSpeed(leftDistance, gpioControler.leftMotor);
                            }
                            break;
                        case LEFT:
                            break;
                        case RIGHT:
                            leftMotorSpeed = checkMotorSpeed(25, gpioControler.leftMotor);
                            break;
                        case NONE:
                            gpioControler.leftMotor.setDirection(0);
                            leftMotorSpeed = checkMotorSpeed(100, gpioControler.leftMotor);

                            break;
                        default:

                    }
                    System.out.print("\033[H\033[2J");
                    System.out.println("--> L = " + leftDistance + " cm / R = " + rightDistance + " cm | "
                            + typeObstacle.libelle + " | " + leftMotorSpeed + "%");
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
     * checkMotorSpeed
     *
     * @param motor
     */
    private int checkMotorSpeed(Integer distance, Motor motor) {
        if (distance >= 0 && distance <= 10) {
            motor.setSpeed(0);
            return 0;
        } else if (distance >= 10 && distance <= 25) {
            motor.setSpeed(20);
            return 25;
        } else if (distance >= 25 && distance <= 50) {
            motor.setSpeed(50);
            return 50;
        } else if (distance >= 100) {
            motor.setSpeed(100);
            return 100;
        } else {
            motor.setSpeed(distance);
            return distance;
        }
    }

    /**
     * checkObstacle
     *
     * @param leftDist
     * @param rightDist
     * @return
     */
    private Constants.OBSTACLE_TYPE checkObstacle(int leftDist, int rightDist) {
        int diff = Math.abs(leftDist - rightDist);
        // si difference de 50cm et distance < 1 metre
        if (diff >= 50 && (leftDist <= 100 || rightDist <=100)) {
            // obstalce detecté
            if (leftDist > rightDist) {
                return Constants.OBSTACLE_TYPE.RIGHT;
            } else {
                return Constants.OBSTACLE_TYPE.LEFT;
            }
        } else {
            if (leftDist <= 50 || rightDist <=50) {
                return Constants.OBSTACLE_TYPE.WALL;
            }
        }
        return Constants.OBSTACLE_TYPE.NONE;
    }


}
