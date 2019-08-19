import com.pi4j.io.gpio.*;
import com.pi4j.util.Console;

public class GpioControler {

    public final Motor leftMotor;

    private final Console console;

    /**
     * GpioControler
     */
    public GpioControler() {
        console = new Console();
        console.title("<-- Landroid Project -->", "Init GpioControler...");

        // Set Left Motor
        leftMotor = new Motor("Left Motor", RaspiPin.GPIO_23, RaspiPin.GPIO_02, RaspiPin.GPIO_03);
    }

    /**
     * testGpio
     * @throws InterruptedException
     */
    public void testGpio() throws InterruptedException {
        testMotor();
    }

    /**
     * testMotor
     */
    private void testMotor() {
        try {
            console.title("<-- Landroid Project -->", "Test Motor PWM");
            for(int i=0; i<100; i++){
                leftMotor.setSpeed(i);
                console.println("PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(100);
            }
            console.println("PWM rate is: FULL SPEED !");
            Thread.sleep(10000);

            for(int i=100; i>0; i--){
                leftMotor.setSpeed(i);
                console.println("PWM rate is: " + leftMotor.getSpeed());
                Thread.sleep(50);
            }
            leftMotor.stopMotor();
            console.println("Test with 100 Steep");
            leftMotor.controlMotorWithSteep(100,20,0);
            console.println(" ... Finish !");
        } catch (Exception ex) {
            console.println(ex);
        }
        leftMotor.stopMotor();
    }

    /**
     * testUltrasonic
     * @param echoPin
     * @param trigPin
     */
    public int getDistance(Pin echoPin, Pin trigPin) {
        try {
            PiJavaUltrasonic sonic = new PiJavaUltrasonic(
                    echoPin.getAddress(),//ECO PIN (physical 11)
                    trigPin.getAddress(),//TRIG PIN (pysical 22)
                    1000,//REJECTION_START ; long (nano seconds)
                    23529411 //REJECTION_TIME ; long (nano seconds)
            );
            int distance = sonic.getDistance();
            System.out.println("distance " + sonic.getDistance() + "mm");
            return distance;
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
        return 0;
    }
}
