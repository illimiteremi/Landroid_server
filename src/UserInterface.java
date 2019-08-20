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

    public static String wavStart = "/wav/start.wav";
    public static String wavStop = "/wav/stop.wav";
    public static String wavHalt = "/wav/goodbye.wav";

    /**
     * speak
     * 
     * @param wavFile
     */
    public static void speak(String wavFile) {
        AudioInputStream audioIn;
        try {
            audioIn = AudioSystem.getAudioInputStream(new File(wavFile));
            Clip clip;
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            System.out.println("Speak Error : " + e);
        }
    }

}
