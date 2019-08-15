public class Main {

    public static void main(String[] args) {

        final String host = "192.168.0.48";
        final int port    = 8080;

        LandroidServer ts = new LandroidServer(host, port);
        ts.open();

        System.out.println("Serveur initialisé. Landroid en ecoute...");
        System.out.println(" ... PRESS <CTRL-C> TO STOP THE PROGRAM.");
    }
}