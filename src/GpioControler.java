import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.Console;
import java.util.logging.Logger;

public class GpioControler {

    private GpioController gpio;

    public final Motor leftMotor, rightMotor;

    public final GpioPinDigitalInput gpioStartAndStop;
    public final ModeAlone modeAlone
            ;
    private Boolean isStarted = false;

    /**
     * GpioControler
     */
    public GpioControler() {
        System.out.println("<-- Landroid Project --> Init GpioControler...");
        gpio = GpioFactory.getInstance();

        // Set Left Motor
        leftMotor = new Motor("Left Motor", RaspiPin.GPIO_23, RaspiPin.GPIO_02, RaspiPin.GPIO_03);
        rightMotor = new Motor("Right Motor", RaspiPin.GPIO_12, RaspiPin.GPIO_13, RaspiPin.GPIO_14);

        // Set startAndStop
        gpioStartAndStop = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);
        gpioStartAndStop.setShutdownOptions(true);
        controlStartAndStop();

        // Set Mode Alone
        modeAlone = new ModeAlone(this);
    }

    /**
     * testGpio
     * 
     * @throws InterruptedException
     */
    public void testGpio() throws InterruptedException {
        testMotor();
    }

    /**
     * stopAll
     */
    public void stopAll() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }

    /**
     * controlMotorWithSteep
     */
    private void controlStartAndStop() {
        gpioStartAndStop.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                System.out.println("Button Pressed !");
                if (isStarted) {
                    isStarted = false;
                    UserInterface.speak(UserInterface.wavStop);
                    //clientProcessor.modeAlone.stopModeAlone();
                } else {
                    isStarted = true;
                    UserInterface.speak(UserInterface.wavStop);
                    //clientProcessor.modeAlone.toString();
                }
            }
        });
    }

    /**
     * testMotor
     */
    private void testMotor() {
        try {
            System.out.println("<-- Landroid Project --> Test Motor PWM");
            for (int i = 0; i < 100; i++) {
                leftMotor.setSpeed(i);
                System.out.println("--> PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(100);
            }
            System.out.println("--> PWM rate is: FULL SPEED !");
            Thread.sleep(10000);

            for (int i = 100; i > 0; i--) {
                leftMotor.setSpeed(i);
                System.out.println("--> PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(50);
            }
/*
            leftMotor.stopMotor();
            console.println("Test with 100 steep forward");
            leftMotor.controlMotorWithSteep(20, 1, 200);

            //console.println("Test with 100 steep backward");
            //leftMotor.controlMotorWithSteep(100, 20, 0);
*/
            System.out.println("--> Finish !");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        leftMotor.stopMotor();
    }
}
