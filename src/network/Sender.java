package network;

import exception.ConnectionException;
import exception.ExceptionType;
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

    private void send(Message message, ObjectOutputStream os) throws ConnectionException {
        synchronized (os) { // ObjectOutputStream에 동기화 적용
            try {
                os.writeObject(message);
            } catch (IOException e) {
                throw new ConnectionException(ExceptionType.MESSAGE_SEND_ERROR);
            }
            System.out.println(message.getType());
            System.out.println(message.getMsgDTO());
        }
    }

    public void send(Message message, int id) {
        System.out.println("send to " + id);
        ObjectOutputStream os = connectionManager.getOutputStream(id);
        try {
            send(message, os);
        } catch (ConnectionException e) {
            System.err.println("메시지 송신 불가: 클라이언트와 연결이 끊어졌습니다.");
            connectionManager.closeConnection(id);
        }
    }

    public void sendToAll(Message message, List<Integer> idList) {
        idList.forEach(id -> {send(message, id);});
    }
}