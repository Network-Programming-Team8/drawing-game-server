package network;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import static message.MessageType.*;

import domain.User;
import exception.ErrorType;
import service.UserManager;
import service.GameRoomManager;
import handler.MessageHandler;
import message.Message;
import dto.event.client.ClientLoginEvent;
import dto.event.server.ServerLoginEvent;
import dto.event.server.ServerErrorEvent;
import exception.GameServerException;


public class ClientDispatcher implements Runnable{
    private final UserManager userManager;
    private final MessageHandler messageHandler;

    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private boolean isConnected = false;

    public ClientDispatcher(Socket socket, GameRoomManager roomManager, UserManager userManager){
        this.userManager = userManager;
        messageHandler = new MessageHandler(roomManager);
        try{
            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("client connected");
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
        Message message;
        try {
            message = (Message) fromClient.readObject();
            System.out.println("메세지 수신됨: " + message.getType());
        } catch (IOException | ClassNotFoundException e) {
            throw new GameServerException(ErrorType.MESSAGE_RECEIVE_ERROR);
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
        Message message = new Message(SERVER_ERROR_EVENT, new ServerErrorEvent(errorMessage));
        sendMessageToClient(message);
        System.err.println("에러 메세지 송신됨: " + errorMessage);
    }

    private void receiveAndSendMessageWith(User user) throws GameServerException{
        Message msgFromClient = getMessageFromClient();
        Message msgToClient = messageHandler.handle(msgFromClient, user);
        sendMessageToClient(msgToClient);
    }

    private User createUser() throws GameServerException {
        User user;
        Message msgFromClient = getMessageFromClient();

        if(msgFromClient.getType() != CLIENT_LOGIN_EVENT) {
            throw new GameServerException(ErrorType.INVALID_INPUT, "최초 메세지는 로그인 요청이어야 합니다.");
        }

        ClientLoginEvent clientLoginEvent = (ClientLoginEvent) msgFromClient.getMsgDTO();
        user = userManager.createUser(clientLoginEvent.getNickName());
        ServerLoginEvent serverLoginEvent = new ServerLoginEvent(user.getNickname(), user.getId());

        Message msgToClient = new Message(SERVER_LOGIN_EVENT, serverLoginEvent);
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
