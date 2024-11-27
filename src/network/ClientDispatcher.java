package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import service.GameRoomManager;
import handler.MessageHandler;
import service.UserManager;
import dto.event.client.ClientLoginEventDTO;
import dto.event.server.ServerErrorEventDTO;
import exception.GameServerException;
import message.Message;
import domain.User;
import dto.event.server.ServerLoginEventDTO;

import static message.MessageType.*;

public class ClientDispatcher implements Runnable{
    private final UserManager userManager;
    private final MessageHandler messageHandler;

    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    boolean isConnected = false;

    public ClientDispatcher(Socket socket, GameRoomManager roomManager, UserManager userManager){
        this.userManager = userManager;
        messageHandler = new MessageHandler(roomManager);
        try{
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());
            isConnected = true;
        } catch (Exception ex){
            System.err.println("클라이언트 접속 중 오류");
        }
    }

    private void closeConnection() {
        try {
            if (fromClient != null) fromClient.close();
            if (toClient != null) toClient.close();
        } catch (IOException e) {
            System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
        }
        isConnected = false;
        System.out.println("클라이언트 연결 종료");
    }

    private Message getMessageFromClient() throws GameServerException {
        Message message = null;
        try {
            message = (Message) fromClient.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameServerException("수신 중 오류");
        }
        return message;
    }

    private void sendMessageToClient(Message message) {
        try {
            toClient.writeObject(message);
        } catch (IOException e) {
            System.err.println("송신 중 오류: 클라이언트 연결을 종료합니다.");
            closeConnection();
        }
    }

    private void sendAndLogErrorMessage(Exception e) {
        if (!isConnected) {
            System.err.println("에러 메시지 송신 불가: 클라이언트와 연결이 끊어졌습니다.");
        }
        String errorMessage = e.getMessage();
        Message message = new Message(SERVER_ERROR_EVENT, new ServerErrorEventDTO(errorMessage));
        sendMessageToClient(message);
        System.err.println("에러 메세지 송신됨: " + errorMessage);
    }

    private void receiveAndSendMessageWith(User user) throws GameServerException{
        Message msgFromClient = getMessageFromClient();
        Message msgToClient = messageHandler.handle(msgFromClient, user);
        sendMessageToClient(msgToClient);
    }

    private User createUser() throws GameServerException {
        User user = null;
        Message msgFromClient = getMessageFromClient();

        if(msgFromClient.getType() != CLIENT_LOGIN_EVENT) {
            throw new GameServerException("최초 메세지는 로그인 요청이어야 합니다");
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
        User user = null;
        while(user == null && isConnected) {
            try {
                user = createUser();
            } catch (GameServerException e) {
                sendAndLogErrorMessage(e);
            }
        }
        while(isConnected){
            try {
                receiveAndSendMessageWith(user);
            } catch (GameServerException e) {
                sendAndLogErrorMessage(e);
            }
        }

        closeConnection();
    }
}
