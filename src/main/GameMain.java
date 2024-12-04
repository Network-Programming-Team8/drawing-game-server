package main;

import handler.MessageHandler;
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
        MessageHandler messageHandler = new MessageHandler(gameRoomManager, sender);
        ConnectionListener cl = new ConnectionListener(connectionManager, sender, messageHandler);
        cl.waitForConnections();
    }
}