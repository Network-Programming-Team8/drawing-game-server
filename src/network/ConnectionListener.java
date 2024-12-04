package network;

import manager.GameRoomManager;
import manager.ConnectionManager;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {

    private final GameRoomManager roomManager;
    private final ConnectionManager connectionManager;

    public ConnectionListener(GameRoomManager roomManager, ConnectionManager connectionManager){
        this.roomManager = roomManager;
        this.connectionManager = connectionManager;
    }

    public void waitForConnections(){
        try{
            ServerSocket server = new ServerSocket(10001);
            System.out.println("Created Server Socket at port 10001");
            while(true){
                Socket socket = server.accept();

                Thread connection = new Thread(new ClientDispatcher(socket, roomManager, connectionManager));
                connection.start();
            }
        } catch (Exception ex){
            System.err.println("서버 소켓 생성 및 클라이언트의 접속 대기 중 오류");
        }
    }
}
