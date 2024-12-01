package main;

import network.ConnectionListener;
import service.GameRoomManager;
import service.UserManager;

public class GameMain {
    public static void main(String[] args) {
        System.out.println("Game Main Start");
        GameRoomManager roomManager = new GameRoomManager();
        UserManager userManager = new UserManager();
        ConnectionListener cl = new ConnectionListener(roomManager, userManager);
        cl.waitForConnections();
    }
}