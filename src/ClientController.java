import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController implements Runnable{

    private Socket socket = null;
    private ObjectInputStream fromClient = null;
    private ObjectOutputStream toClient = null;
    private User user = null;

    public ClientController(Socket socket){

        this.socket = socket;

        try{
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());

            messageHandler.handle(fromClient.readObject(), );  //CLIENT_LOGIN_EVENT
            user = new User((String) nickname);
//          toClient.writeObject();  //SERVER_LOGIN_EVENT

        } catch (Exception ex){
            System.out.println("클라이언트 접속 중 오류");
        }
    }

    @Override
    public void run(){


    }
}
