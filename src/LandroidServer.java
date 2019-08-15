import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class LandroidServer {

    private final int port;
    private final String host;
    private boolean isRunning   = true;

    private ServerSocket server = null;
    private GpioControler gpioControler;


    public LandroidServer(String pHost, int pPort){

        host = pHost;
        port = pPort;

        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
            gpioControler = new GpioControler();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Start Server
    public void open() {

        Thread t = new Thread(() -> {

            while (isRunning) {
                try {
                    // waiting client connexion...
                    Socket client = server.accept();
                    Thread thread = new Thread(new ClientProcessor(client, gpioControler));
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                server = null;
                System.err.println("IOException : " + e.getMessage());
            }
        });
        t.start();
    }

    public void close(){
        isRunning = false;
    }
}