package main;

import Logic.ConnectionListener;
import Logic.GameRoomManager;
import Logic.UserManager;

public class GameMain {
    public static void main(String[] args) {

        GameRoomManager roomManager = new GameRoomManager();
        UserManager userManager = new UserManager();
        ConnectionListener cl = new ConnectionListener(roomManager, userManager);

        cl.waitForConnections();
    }
}