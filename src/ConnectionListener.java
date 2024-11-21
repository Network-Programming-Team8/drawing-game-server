import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {

    private ServerSocket server = null;
    private GameRoomManager roomManager = null;

    public ConnectionListener(ServerSocket server, GameRoomManager roomManager){

        this.server = server;
        this.roomManager = roomManager;
    }

    void waitForConnections(){

        try{
            while(true){

                Socket socket = server.accept();
                Thread client = new Thread(new ClientController(socket));
                client.start();
            }

        } catch (Exception ex){

            System.out.println("클라이언트의 접속 대기 중 오류"); }
    }
}
