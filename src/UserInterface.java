import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;

public class UserInterface {

    private GpioController gpio;
    private GpioPinDigitalInput gpioStartAndStop;
    private Boolean isStarted;

    private static String wavStart = "/wav/start.wav";
    private static String wavStop = "/wav/stop.wav";
    private static String wavHalt = "/wav/goodbye.wav";

    private Console console;

    /**
     * UserInterface Controler
     */
    public UserInterface(Pin pinStartAndStop) {
        console = new Console();

        console.title("<-- Landroid Project -->", "Init User Interface");

        // Create gpio controller for motor
        try {
            gpio = GpioFactory.getInstance();

            if (pinStartAndStop != null) {
                // Button Start / Stop
                gpioStartAndStop = gpio.provisionDigitalInputPin(pinStartAndStop, PinPullResistance.PULL_UP);
                gpioStartAndStop.setShutdownOptions(true);

                // Check Value;
                isStarted = gpioStartAndStop.getState().getValue() == 1 ? true : false;

                // Start Listener
                controlStartAndStop();
            }
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

    /**
     * speak
     * 
     * @param wavFile
     */
    public void speak(String wavFile) {
        AudioInputStream audioIn;
        try {
            audioIn = AudioSystem.getAudioInputStream(new File(wavFile));
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            console.println("Speak Error : " + e);
        }
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
