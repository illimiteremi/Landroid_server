import com.pi4j.util.Console;

public class Main {

    public static void main(String[] args) {

        final String host = "192.168.0.30";
        final int port    = 8080;

        LandroidServer ts = new LandroidServer(host, port);
        ts.open();

        Console console = new Console();
        console.title("<-- Landroid Project -->", "Landroid en ecoute...");
        System.out.println(" ... PRESS <CTRL-C> TO STOP THE PROGRAM.");
    }
}