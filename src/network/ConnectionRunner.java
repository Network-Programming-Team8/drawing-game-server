package network;

import java.io.IOException;

import static message.MessageType.*;

import domain.User;
import exception.ErrorType;
import exception.ExceptionHandler;
import manager.ConnectionManager;
import message.MessageHandler;
import message.Message;
import dto.event.client.ClientLoginEvent;
import dto.event.server.ServerLoginEvent;
import exception.GameServerException;


public class ConnectionRunner implements Runnable{
    private final ConnectionManager connectionManager;
    private final MessageHandler messageHandler;
    private final ExceptionHandler exceptionHandler;
    private final Sender sender;
    private final int id;

    public ConnectionRunner(int id, ConnectionManager connectionManager, Sender sender,
                            MessageHandler messageHandler, ExceptionHandler exceptionHandler){
        this.connectionManager = connectionManager;
        this.sender = sender;
        this.messageHandler = messageHandler;
        this.id = id;
        this.exceptionHandler = exceptionHandler;
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

    private void handleMessageWith(User user) throws GameServerException, InterruptedException {
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
                exceptionHandler.handle(e, id);
            }
        }
        while(connectionManager.hasConnection(id)){
            try {
                handleMessageWith(connectionManager.getUser(id));
            } catch (GameServerException | InterruptedException e) {
                exceptionHandler.handle(e, id);
            }
        }
        closeConnection();
    }
}
