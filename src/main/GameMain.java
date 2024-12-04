package main;

import network.ConnectionListener;
import manager.GameRoomManager;
import manager.ConnectionManager;

public class GameMain {
    public static void main(String[] args) {
        System.out.println("Game Main Start");
        GameRoomManager roomManager = new GameRoomManager();
        ConnectionManager connectionManager = new ConnectionManager();
        ConnectionListener cl = new ConnectionListener(roomManager, connectionManager);
        cl.waitForConnections();
    }
}