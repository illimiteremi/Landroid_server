import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;

public class UserInterface {

    private GpioController gpio;
    private GpioPinDigitalOutput gpioStartAndStop;
    private Boolean isStarted;

    private Console console;

    /**
     * UserInterface Controler
     */
    public UserInterface(Pin startAndStop) {
        console = new Console();

        console.title("<-- Landroid Project -->", "Init User Interface");

        // Create gpio controller for motor
        try {
            gpio = GpioFactory.getInstance();
            // Button Start / Stop
            gpioStartAndStop = gpio.provisionDigitalInputPin(pinSteeper, PinPullResistance.PULL_UP);
            gpioStartAndStop.setShutdownOptions(true);

            // Check Value;
            isStarted = gpioStartAndStop.getState.getState == 1 ? true : false;

            // Start Listener
            controlStartAndStop();

        } catch (Exception ex) {
            console.println("Motor Error : " + ex);
        }
    }

    /**
     * controlMotorWithSteep
     */
    private void controlStartAndStop() {

        gpioStartAndStop.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                console.println("Button Pressed !");
                if (isStarted) {
                    isStarted = false;
                    stopLandroid();
                } else {
                    isStarted = true;
                    startLandroid();
                }
            }
        });
    }

    public void startLandroid() {
        console.println("Start Landroid Robot...");
    }

    public void stopLandroid() {
        // stop all GPIO activity/threads by shutting down the GPIO controller
        console.println("Stop all GPIO activity !");
        gpio.shutdown();
    }

}
