package network;

import exception.ConnectionError;
import exception.ErrorType;
import handler.MessageHandler;
import manager.GameRoomManager;
import manager.ConnectionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener {

    private final ConnectionManager connectionManager;
    private final Sender sender;
    private final MessageHandler messageHandler;

    public ConnectionListener(ConnectionManager connectionManager, Sender sender, MessageHandler messageHandler){
        this.connectionManager = connectionManager;
        this.sender = sender;
        this.messageHandler = messageHandler;
    }

    public void waitForConnections(){
        try{
            ServerSocket server = new ServerSocket(10001);
            System.out.println("Created Server Socket at port 10001");
            while(true){
                Socket socket = server.accept();
                int connectionId = connect(socket);
                Thread connectionThread = new Thread(new ConnectionRunner(connectionId, connectionManager, sender, messageHandler));
                connectionThread.start();
            }
        } catch (Exception ex){
            System.err.println("서버 소켓 생성 및 클라이언트의 접속 대기 중 오류");
        }
    }

    private int connect(Socket socket) throws ConnectionError {
        try { //TODO 동시성문제?
            ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
            return connectionManager.addConnection(toClient, fromClient);
        } catch (IOException e) {
            throw new ConnectionError(ErrorType.FAILED_TO_CONNECT);
        }
    }
}
