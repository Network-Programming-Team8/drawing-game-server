package Logic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Message.Message;
import Message.ServerLoginEventDTO;

public class ClientController implements Runnable{

    private final MessageHandler msgHandler = null;
    private ObjectInputStream fromClient = null;
    private ObjectOutputStream toClient = null;
    private User user = null;

    public ClientController(Socket socket, GameRoomManager roomManager, UserManager userManager){

        MessageHandler msgHandler = new MessageHandler(roomManager, userManager);

        try{
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());

            //Login
            Message msgFromClient = (Message) fromClient.readObject();  //Receive CLIENT_LOGIN_EVENT_MESSAGE
            Message msgToClient = msgHandler.handle(msgFromClient);     //Create User and SERVER_LOGIN_EVENT_MESSAGE
            toClient.writeObject(msgToClient);                          //Send SERVER_LOGIN_EVENT_MESSAGE

            ServerLoginEventDTO svr_login_dto = (ServerLoginEventDTO) msgToClient.getMsgDTO();
            user = userManager.getUser(svr_login_dto.getId());

        } catch (Exception ex){
            System.out.println("클라이언트 접속 중 오류");
        }
    }

    public void receiveAndSendMessage(){

        try{
            Message msgFromClient = (Message) fromClient.readObject();
            Message msgToClient = msgHandler.handle(msgFromClient, user);
            toClient.writeObject(msgToClient);

        } catch (Exception ex){
            System.out.println("통신 중 오류");
        }

    }

    @Override
    public void run(){

        while(true){
            receiveAndSendMessage();
        }
    }
}
