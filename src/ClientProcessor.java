import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientProcessor implements Runnable{

    private Socket sock;
    private GpioControler gpioControler;


    public ClientProcessor(Socket pSock, GpioControler gpioControler){
        this.sock = pSock;
        this.gpioControler = gpioControler;
    }

    public void run(){
        while(!sock.isClosed()){

            try {
                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                DataInputStream in   = new DataInputStream(sock.getInputStream());

                // On attend la demande du client
                byte    cmd   =  in.readByte();
                Integer speed = Integer.valueOf(in.readByte());

                // On affiche quelques infos, pour le débuggage
                String debug = "Commande reçue : " + cmd + " - " + Constants.COMMANDE.getValue(cmd).getMessage() + " - Vitesse = " + speed;
                System.out.println(debug);

                // On traite la demande du client en fonction de la commande envoyée
                switch (Constants.COMMANDE.getValue(cmd)) {
                    case COMMANDE_UNKNOW      :
                        out.write(Constants.RESPONSE.COMMANDE_UNKNOW.getCode());
                        break;
                    case TEST_LANDROID        :
                        gpioControler.testGpio();
                        break;
                    case DIRECTION_CENTER     :
                        break;
                    case DIRECTION_LEFT       :
                        break;
                    case DIRECTION_LEFT_UP    :
                        break;
                    case DIRECTION_UP         :
                        gpioControler.setBrushSpeed(speed);
                         break;
                    case DIRECTION_UP_RIGHT   :
                        break;
                    case DIRECTION_RIGHT      :
                        break;
                    case DIRECTION_RIGHT_DOWN :
                        break;
                    case DIRECTION_DOWN       :
                        gpioControler.setBrushSpeed(speed);
                        break;
                    case DIRECTION_DOWN_LEFT  :
                        break;
                    case BRUSH_START          :
                        gpioControler.startBrush(1);
                        break;
                    case BRUSH_STOP           :
                        gpioControler.startBrush(0);
                        break;
                    case START_ALONE_MODE     :
                        break;
                    case STOP_ALONE_MODE      :
                        out.write(Constants.RESPONSE.SUCCESS.getCode());
                        break;
                    default:
                        out.write(Constants.RESPONSE.SUCCESS.getCode());
                }
                out.flush();
                // Close connexion
                out.close();
                in.close();
                sock.close();

            } catch(SocketException e) {
                System.err.println("SocketException : " + e.getMessage());
                break;
            } catch (Exception e) {
                break;
            }
         }
    }
}