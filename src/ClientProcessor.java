import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientProcessor implements Runnable {

    private final Socket sock;
    public final GpioControler gpioControler;

    public ClientProcessor(Socket pSock, GpioControler gpioControler) {
        this.sock = pSock;
        this.gpioControler = gpioControler;
     }

    public void run() {
        while (!sock.isClosed()) {

            try {
                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                DataInputStream in = new DataInputStream(sock.getInputStream());

                // On attend la demande du client
                byte cmd = in.readByte();
                Integer speed = Integer.valueOf(in.readByte());

                // On affiche quelques infos, pour le débuggage
                String debug = "--> Commande reçue : " + cmd + " - " + Constants.COMMANDE.getValue(cmd).getMessage()
                        + " - Vitesse = " + speed;
                System.out.println(debug);

                // On traite la demande du client en fonction de la commande envoyée
                switch (Constants.COMMANDE.getValue(cmd)) {
                case COMMANDE_UNKNOW:
                    out.write(Constants.RESPONSE.COMMANDE_UNKNOW.getCode());
                    break;
                case TEST_LANDROID:
                    gpioControler.testGpio();
                    break;
                case DIRECTION_CENTER:
                    gpioControler.leftMotor.stopMotor();
                    gpioControler.rightMotor.stopMotor();
                    break;
                case DIRECTION_LEFT:
                    gpioControler.leftMotor.setSpeed(speed);
                    gpioControler.rightMotor.setSpeed(0);
                    break;
                case DIRECTION_LEFT_UP:
                    gpioControler.leftMotor.controlMotor(speed, 1);
                    gpioControler.rightMotor.setSpeed(0);
                    break;
                case DIRECTION_UP:
                    gpioControler.leftMotor.controlMotor(speed, 1);
                    gpioControler.rightMotor.controlMotor(speed, 1);
                    break;
                case DIRECTION_UP_RIGHT:
                    gpioControler.rightMotor.controlMotor(speed, 1);
                    gpioControler.leftMotor.setSpeed(0);
                    break;
                case DIRECTION_RIGHT:
                    gpioControler.leftMotor.setSpeed(0);
                    gpioControler.rightMotor.setSpeed(speed);
                    break;
                case DIRECTION_RIGHT_DOWN:
                    gpioControler.leftMotor.setSpeed(0);
                    gpioControler.rightMotor.controlMotor(speed,0);
                    break;
                case DIRECTION_DOWN:
                    gpioControler.leftMotor.controlMotor(speed, 0);
                    gpioControler.rightMotor.controlMotor(speed,0);
                    break;
                case DIRECTION_DOWN_LEFT:
                    gpioControler.leftMotor.controlMotor(speed, 0);
                    gpioControler.rightMotor.setSpeed(0);
                    break;
                case BRUSH_START:
                    //gpioControler.leftMotor.controlMotor(50, 0);
                    break;
                case BRUSH_STOP:
                    //gpioControler.leftMotor.stopMotor();
                    break;
                case START_ALONE_MODE:
                    gpioControler.modeAlone.startModeAlone();
                    break;
                case STOP_ALONE_MODE:
                    gpioControler.modeAlone.stopModeAlone();
                    break;
                case HALT_SYSTEM:
                    gpioControler.stopAll();
                    Runtime.getRuntime().exec("sudo shutdown -h now");
                    break;
                case STOP_ALL:
                    gpioControler.modeAlone.stopModeAlone();
                    gpioControler.stopAll();
                    break;
                default:
                    out.write(Constants.RESPONSE.SUCCESS.getCode());
                }
                out.flush();
                // Close connexion
                out.close();
                in.close();
                sock.close();
            } catch (SocketException e) {
                System.out.println("SocketException : " + e.getMessage());
                break;
            } catch (Exception e) {
                break;
            }
        }
    }
}