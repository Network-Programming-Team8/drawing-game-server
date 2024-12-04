package network;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import static message.MessageType.*;

import domain.User;
import exception.ErrorType;
import manager.ConnectionManager;
import manager.GameRoomManager;
import handler.MessageHandler;
import message.Message;
import dto.event.client.ClientLoginEvent;
import dto.event.server.ServerLoginEvent;
import dto.event.server.ServerErrorEvent;
import exception.GameServerException;


public class ClientDispatcher implements Runnable{
    private final ConnectionManager connectionManager;
    private final MessageHandler messageHandler;
    private final Sender sender;

    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private boolean isConnected = false;

    public ClientDispatcher(Socket socket, GameRoomManager roomManager, ConnectionManager connectionManager){
        this.connectionManager = connectionManager;
        sender = new Sender(roomManager, connectionManager);
        messageHandler = new MessageHandler(roomManager, sender);
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
            sender.send(message, toClient);
        } catch (RuntimeException e) {
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

    private void handleMessageWith(User user) throws GameServerException{
        Message msgFromClient = getMessageFromClient();
        messageHandler.handle(msgFromClient, user);
    }

    private User createUser() throws GameServerException {
        User user;
        Message msgFromClient = getMessageFromClient();

        if(msgFromClient.getType() != CLIENT_LOGIN_EVENT) {
            throw new GameServerException(ErrorType.INVALID_INPUT, "최초 메세지는 로그인 요청이어야 합니다.");
        }

        ClientLoginEvent clientLoginEvent = (ClientLoginEvent) msgFromClient.getMsgDTO();
        user = connectionManager.createUser(clientLoginEvent.getNickName(), toClient);
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
                handleMessageWith(user);
            } catch (GameServerException e) {
                sendAndLogErrorMessage(e);
            }
        }

        closeConnection();
    }
}
