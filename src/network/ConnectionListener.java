package network;

import Logic.GameRoomManager;
import Logic.UserManager;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {

    private GameRoomManager roomManager = null;
    private UserManager userManager = null;

    public ConnectionListener(GameRoomManager roomManager, UserManager userManager){
        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    public void waitForConnections(){

        try{
            ServerSocket server = new ServerSocket(10001);

            while(true){
                Socket socket = server.accept();
                Thread connection = new Thread(new ClientDispatcher(socket, roomManager, userManager));
                connection.start();
            }

        } catch (Exception ex){
            System.err.println("서버 소켓 생성 및 클라이언트의 접속 대기 중 오류");
        }
    }
}
