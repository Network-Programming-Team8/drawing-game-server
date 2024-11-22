import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Message.Message;
import Message.DTO;

public class ClientController implements Runnable{

    private final MessageHandler msgHandler = null;
    private ObjectInputStream fromClient = null;
    private ObjectOutputStream toClient = null;

    public ClientController(Socket socket, GameRoomManager roomManager, UserManager userManager){

        MessageHandler msgHandler = new MessageHandler(roomManager, userManager);

        try{
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());

            /*
            Message msgFromClient = (Message) fromClient.readObject();
            String nickname = msgHandler.handle(msgFromClient);  //CLIENT_LOGIN_EVENT
            User user = new User((String) nickname);

            DTO dto = ServerLoginEventDTO(nickname, 0);
            Message msgToClient = new Message(dto);
            toClient.writeObject(msgToClient);  //SERVER_LOGIN_EVENT
            */

        } catch (Exception ex){
            System.out.println("클라이언트 접속 중 오류");
        }
    }

    public void receiveAndSendMessage(){

        try{
            Message msgFromClient = (Message) fromClient.readObject();
            msgHandler.handle(msgFromClient);

            Message msgToClient = null;
            toClient.writeObject(msgToClient);

        } catch (Exception ex){
            System.out.println("통신 중 오류");
        }

    }

    @Override
    public void run(){


    }
}
