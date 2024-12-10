package main;

import exception.ExceptionHandler;
import manager.MessageHandlerManager;
import network.ConnectionListener;
import manager.GameRoomManager;
import manager.ConnectionManager;
import network.Sender;

public class GameMain {
    public static void main(String[] args) {
        System.out.println("Game Main Start");
        GameRoomManager gameRoomManager = new GameRoomManager();
        ConnectionManager connectionManager = new ConnectionManager(gameRoomManager);
        Sender sender = new Sender(connectionManager);
        ExceptionHandler exceptionHandler = new ExceptionHandler(sender, connectionManager);
        MessageHandlerManager messageHandlerManager = new MessageHandlerManager(gameRoomManager, sender, exceptionHandler);
        ConnectionListener cl = new ConnectionListener(connectionManager, sender, messageHandlerManager, exceptionHandler);
        cl.waitForConnections();
    }
}