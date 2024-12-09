package network;

import exception.ConnectionException;
import exception.ErrorType;
import exception.ExceptionHandler;
import handler.MessageHandler;
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
    private final ExceptionHandler exceptionHandler;

    public ConnectionListener(ConnectionManager connectionManager, Sender sender,
                              MessageHandler messageHandler, ExceptionHandler exceptionHandler){
        this.connectionManager = connectionManager;
        this.sender = sender;
        this.messageHandler = messageHandler;
        this.exceptionHandler = exceptionHandler;
    }

    public void waitForConnections(){
        try{
            ServerSocket server = new ServerSocket(10001);
            System.out.println("Created Server Socket at port 10001");
            while(true){
                Socket socket = server.accept();
                int connectionId = connect(socket);
                Thread connectionThread = new Thread(
                        new ConnectionRunner(connectionId, connectionManager, sender,
                                messageHandler, exceptionHandler));
                connectionThread.start();
            }
        } catch (Exception e){
            exceptionHandler.handle(e);
        }
    }

    private int connect(Socket socket) throws ConnectionException {
        try {
            ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
            return connectionManager.addConnection(toClient, fromClient);
        } catch (IOException e) {
            throw new ConnectionException(ErrorType.FAILED_TO_CONNECT);
        }
    }
}
