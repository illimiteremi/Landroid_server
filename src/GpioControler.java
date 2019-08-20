import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class GpioControler {

    public final Motor leftMotor;
    public final UserInterface userInterface;

    private final Console console;

    /**
     * GpioControler
     */
    public GpioControler() {
        console = new Console();
        console.title("<-- Landroid Project -->", "Init GpioControler...");

        // Set Left Motor
        leftMotor = new Motor("Left Motor", RaspiPin.GPIO_23, RaspiPin.GPIO_02, RaspiPin.GPIO_03);

        // Set User Interface
        userInterface = UserInterface(null);
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
    }

    /**
     * testMotor
     */
    private void testMotor() {
        try {
            console.title("<-- Landroid Project -->", "Test Motor PWM");
            for (int i = 0; i < 100; i++) {
                leftMotor.setSpeed(i);
                console.println("PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(100);
            }
            console.println("PWM rate is: FULL SPEED !");
            Thread.sleep(10000);

            for (int i = 100; i > 0; i--) {
                leftMotor.setSpeed(i);
                console.println("PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(50);
            }

            leftMotor.stopMotor();
            console.println("Test with 100 steep forward");
            leftMotor.controlMotorWithSteep(100, 20, 1);

            console.println("Test with 100 steep backward");
            leftMotor.controlMotorWithSteep(100, 20, 0);

            console.println(" ... Finish !");
        } catch (Exception ex) {
            console.err.println(ex);
        }
        leftMotor.stopMotor();
    }
}
