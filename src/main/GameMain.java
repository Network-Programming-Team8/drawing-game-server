package main;

import handler.MessageHandler;
import network.ConnectionListener;
import manager.GameRoomManager;
import manager.ConnectionManager;
import network.Sender;

public class GameMain {
    public static void main(String[] args) {
        System.out.println("Game Main Start");
        GameRoomManager roomManager = new GameRoomManager();
        ConnectionManager connectionManager = new ConnectionManager();
        Sender sender = new Sender(roomManager, connectionManager);
        MessageHandler messageHandler = new MessageHandler(roomManager, sender);
        ConnectionListener cl = new ConnectionListener(connectionManager, sender, messageHandler);
        cl.waitForConnections();
    }
}