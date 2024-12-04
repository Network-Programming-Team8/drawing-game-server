package network;

import exception.ConnectionError;
import exception.ErrorType;
import exception.GameServerException;
import message.Message;
import manager.ConnectionManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class Sender {
    private final ConnectionManager connectionManager;

    public Sender(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void send(Message message, ObjectOutputStream os) throws ConnectionError {
        synchronized (os) { // ObjectOutputStream에 동기화 적용
            try {
                os.writeObject(message);
            } catch (IOException e) {
                throw new ConnectionError(ErrorType.MESSAGE_SEND_ERROR);
            }
        }
    }

    public void send(Message message, int id) {
        ObjectOutputStream os = connectionManager.getOutputStream(id);
        try {
            send(message, os);
        } catch (ConnectionError e) {
            System.err.println("메시지 송신 불가: 클라이언트와 연결이 끊어졌습니다.");
            connectionManager.closeConnection(id);
        }
        System.out.printf("sent to %d: %s%n", id, message.getType());
    }

    public void sendToAll(Message message, List<Integer> idList) {
        idList.forEach(id -> {send(message, id);});
    }
}