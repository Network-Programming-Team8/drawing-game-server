package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Logic.GameRoomManager;
import Logic.MessageHandler;
import Logic.UserManager;
import dto.ClientLoginEventDTO;
import message.Message;
import domain.User;
import dto.ServerLoginEventDTO;

import static message.MessageType.CLIENT_LOGIN_EVENT;
import static message.MessageType.SERVER_LOGIN_EVENT;

public class ClientDispatcher implements Runnable{
    private final GameRoomManager roomManager;
    private final UserManager userManager;
    private final MessageHandler messageHandler;

    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    public ClientDispatcher(Socket socket, GameRoomManager roomManager, UserManager userManager){
        this.roomManager = roomManager;
        this.userManager = userManager;
        messageHandler = new MessageHandler(roomManager, userManager);
        try{
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception ex){
            System.err.println("클라이언트 접속 중 오류");
        }
    }

    private Message getMessageFromClient() {
        Message message = null;
        try {
            message = (Message) fromClient.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("수신 중 오류");
        }
        return message;
    }

    private void sendMessageToClient(Message message) {
        try {
            toClient.writeObject(message);
        } catch (IOException e) {
            System.err.println("송신 중 오류");
        }

    }
    private void receiveAndSendMessageWith(User user){
        Message msgFromClient = getMessageFromClient();
        Message msgToClient = messageHandler.handle(msgFromClient, user);
        sendMessageToClient(msgToClient);
    }

    private User createUser() {
        User user = null;
        Message msgFromClient = getMessageFromClient();

        if(msgFromClient.getType() != CLIENT_LOGIN_EVENT) {
            System.err.println("최초 메세지는 로그인 요청이어야 합니다");
        }

        ClientLoginEventDTO clientLoginEventDTO = (ClientLoginEventDTO) msgFromClient.getMsgDTO();
        user = userManager.createUser(clientLoginEventDTO.getNickName());
        ServerLoginEventDTO serverLoginEventDTO = new ServerLoginEventDTO(user.getNickname(), user.getID());

        Message msgToClient = new Message(SERVER_LOGIN_EVENT, serverLoginEventDTO);
        sendMessageToClient(msgToClient);

        return user;
    }

    @Override
    public void run(){
        User user = createUser();
        while(true){
            receiveAndSendMessageWith(user);
        }
    }
}
