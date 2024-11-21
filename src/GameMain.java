import java.net.ServerSocket;

public class GameMain {
    public static void main(String[] args) {

        try{
            ServerSocket server = new ServerSocket(10001);
            GameRoomManager roomManager = new GameRoomManager();
            ConnectionListener cl = new ConnectionListener(server, roomManager);

            cl.waitForConnections();

        } catch (Exception ex){

            System.out.println("서버 소켓 생성 중 오류");
        }
    }
}