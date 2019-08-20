import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;

public class Motor {

    private GpioController gpio;
    private GpioPinDigitalOutput gpioDirection;
    private GpioPinPwmOutput gpioSpeed;
    private GpioPinDigitalInput gpioSteeper;

    private int rotorSteep;

    private String motorName;

    private Console console;

    /**
     * Motor Controler
     */
    public Motor(String motorName, Pin pinSpeed, Pin pinDirection, Pin pinSteeper) {
        console = new Console();
        this.motorName = motorName;
        console.println("<-- Landroid Project -->", "Init Motor :" + this.motorName);

        // Create gpio controller for motor
        try {
            gpio = GpioFactory.getInstance();
            // Direction
            gpioDirection = gpio.provisionDigitalOutputPin(pinDirection, "direction_motor_" + this.motorName,
                    PinState.LOW);
            // Speed
            gpioSpeed = gpio.provisionPwmOutputPin(pinSpeed, "speed_motor_" + motorName, 0);
            gpioSpeed.setPwmRange(100);
            // Steeper
            if (pinSteeper != null) {
                gpioSteeper = gpio.provisionDigitalInputPin(pinSteeper, PinPullResistance.PULL_UP);
                gpioSteeper.setShutdownOptions(true);
            }
        } catch (Exception ex) {
            console.println("Motor Error : " + ex);
        }
    }

    /**
     * controlMotorWithSteep
     * 
     * @param nbSteep
     * @param speed
     * @param direction
     */
    public synchronized void controlMotorWithSteep (final int speed, final int direction, final int nbSteep) {

        console.println("Number of steep to do : " + nbSteep);
        rotorSteep = 0;

        gpioSteeper.addListener((GpioPinListenerDigital) event -> {
            console.println("state = " + event.getState());
            if (event.getState().isHigh()) {
                rotorSteep++;
                console.println("steep = " + rotorSteep);
                if (rotorSteep == nbSteep) {
                    stopMotor();
                }
            }

        });

        controlMotor(speed, direction);
        while (rotorSteep <= nbSteep){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        console.println("Stop at :" + rotorSteep + " steep(s)");

    }

    /**
     * controlMotor
     * 
     * @param speed     : 1 to 100
     * @param direction : true / flase
     */
    public void controlMotor(int speed, int direction) {
        console.println("<--Pi4J--> Set Motor Speed / Direction");
        setDirection(direction);
        gpioSpeed.setPwm(speed);
    }

    /**
     * setSpeed
     * 
     * @param speed
     */
    public void setSpeed(int speed) {
        gpioSpeed.setPwm(speed);
    }

    /**
     * setDirection
     * 
     * @param direction
     */
    public void setDirection(int direction) {
        if (direction == 1) {
            gpioDirection.high();
        } else {
            gpioDirection.low();
        }
    }

    /**
     * getSpeed
     * 
     * @return
     */
    public int getSpeed() {
        return gpioSpeed.getPwm();
    }

    /**
     * getDirection
     * 
     * @return
     */
    public int getDirection() {
        return gpioDirection.getState().getValue();
    }

    /**
     * stopMotor
     */
    public void stopMotor() {
        gpioSpeed.setPwm(0);
    }
}
