import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.text.DecimalFormat;

public class GpioControler {

    private final GpioController gpio;
    private final GpioPinDigitalOutput pin;


    /**
     * GpioControler
     */
    public GpioControler() {
        System.out.println("<--Pi4J--> GPIO init...");
        // create gpio controller
        gpio = GpioFactory.getInstance();
        // provision gpio pin #01 as an output pin and turn off
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Brush", PinState.LOW);
    }

    /**
     * startBrush
     * @param value
     */
    public void startBrush(int value) {
        System.out.println("<--Pi4J--> GPIO Control Brush...");
        pin.blink(0);
        if (value == 1) {
            pin.high();
            System.out.println("--> GPIO 01 state should be: ON");

        } else {
            pin.low();
            System.out.println("--> GPIO 01 state should be: OFF");
        }
    }

    /**
     * brushSpeed
     * @param speed
     */
    public void setBrushSpeed(int speed) {
        try {
            switch (speed) {
                case 0 :
                    pin.low();
                    System.out.println("<--Pi4J--> Stop Brush");
                    break;
                case 100 :
                    pin.high();
                    System.out.println("<--Pi4J--> Brush speed max");
                    break;
                default:
                    pin.blink(getFrequency(speed));
                    break;
            }

        } catch (Exception e) {
            System.out.println("<--Pi4J--> Error " + e);
        }
    }

    /**
     * testGpio
     * @throws InterruptedException
     */
    public void testGpio() throws InterruptedException {

        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);

        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // turn off gpio pin #01
        pin.low();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01 (should turn on)
        pin.toggle();
        System.out.println("--> GPIO state should be: ON");

        Thread.sleep(5000);

        // toggle the current state of gpio pin #01  (should turn off)
        pin.toggle();
        System.out.println("--> GPIO state should be: OFF");

        Thread.sleep(5000);

        // turn on gpio pin #01 for 1 second and then off
        System.out.println("--> GPIO state should be: ON for only 1 second");
        pin.pulse(1000, true); // set second argument to 'true' use a blocking call

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

        System.out.println("Exiting ControlGpioExample");


    }

    /**
     * getFrequency
     * @param value
     * @return
     */
    private int getFrequency(int value) {
        int timeValue = 100 - value;
        float freq =  ((float) 1 / (timeValue / (float) 1000)) /  (float) 2;
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("<--Pi4J--> GPIO Speed : " + value + "% - " + df.format(freq ) + "Hz");
        return timeValue;
    }
}
