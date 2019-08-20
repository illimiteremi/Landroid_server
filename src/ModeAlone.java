
public class ModeAlone {

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
                    System.out.println("--> Mode Alone In Progress...");
                    gpioControler.leftMotor.controlMotor(50, 1);
                    Thread.sleep(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("!! AloneModeThread Error : " + e);
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
            // leftCapteur = new PiJavaUltrasonic(leftEchoPin.getAddress(),
            // leftTrigPin.getAddress());
            // rightCapteur = new PiJavaUltrasonic(rightEchoPin.getAddress(),
            // rightTrigPin.getAddress());
        } catch (Exception ex) {
            System.out.println("!! ModeAlone Error : " + ex);
        }
    }

    public void startModeAlone() {
        System.out.println("--> startModeAlone...");
        isRunning = true;
        thread = new Thread(new AloneModeThread());
        thread.start();
    }

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

}
