package network;

import java.io.IOException;

import static message.MessageType.*;

import domain.User;
import exception.ErrorType;
import manager.ConnectionManager;
import handler.MessageHandler;
import message.Message;
import dto.event.client.ClientLoginEvent;
import dto.event.server.ServerLoginEvent;
import dto.event.server.ServerErrorEvent;
import exception.GameServerException;


public class ConnectionRunner implements Runnable{
    private final ConnectionManager connectionManager;
    private final MessageHandler messageHandler;
    private final Sender sender;
    private final int id;

    public ConnectionRunner(int id, ConnectionManager connectionManager, Sender sender, MessageHandler messageHandler){
        this.connectionManager = connectionManager;
        this.sender = sender;
        this.messageHandler = messageHandler;
        this.id = id;
    }

    private void closeConnection() {
        connectionManager.closeConnection(id);
    }

    private Message getMessageFromClient() throws GameServerException {
        Message message;
        try {
            message = (Message) connectionManager.getInputStream(id).readObject();
            System.out.println("메세지 수신됨: " + message.getType());
        } catch (IOException | ClassNotFoundException e) {
            throw new GameServerException(ErrorType.MESSAGE_RECEIVE_ERROR);
        }
        return message;
    }

    private void sendMessageToClient(Message message) {
        sender.send(message, id);
    }

    private void sendAndLogErrorMessage(Exception e) {
        String errorMessage = e.getMessage();
        Message message = new Message(SERVER_ERROR_EVENT, new ServerErrorEvent(errorMessage));
        sendMessageToClient(message);
        System.err.println("에러 메세지 송신됨: " + errorMessage);
    }

    private void handleMessageWith(User user) throws GameServerException{
        Message msgFromClient = getMessageFromClient();
        messageHandler.handle(msgFromClient, user);
    }

    private void register() throws GameServerException {
        Message msgFromClient = getMessageFromClient();

        if(msgFromClient.getType() != CLIENT_LOGIN_EVENT) {
            throw new GameServerException(ErrorType.INVALID_INPUT, "최초 메세지는 로그인 요청이어야 합니다.");
        }

        ClientLoginEvent clientLoginEvent = (ClientLoginEvent) msgFromClient.getMsgDTO();

        User user = new User(id, clientLoginEvent.getNickName());

        connectionManager.registerUserTo(user, id);
        ServerLoginEvent serverLoginEvent = new ServerLoginEvent(user.getNickname(), user.getId());

        Message msgToClient = new Message(SERVER_LOGIN_EVENT, serverLoginEvent);
        sendMessageToClient(msgToClient);
    }

    @Override
    public void run(){
        while(connectionManager.hasConnection(id) && !connectionManager.hasUser(id)) {
            try {
                register();
            } catch (GameServerException e) {
                sendAndLogErrorMessage(e);
            }
        }
        while(connectionManager.hasConnection(id)){
            try {
                handleMessageWith(connectionManager.getUser(id));
            } catch (GameServerException e) {
                sendAndLogErrorMessage(e);
            }
        }
        closeConnection();
    }
}
