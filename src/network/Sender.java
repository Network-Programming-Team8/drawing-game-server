package network;

import exception.ConnectionError;
import exception.ErrorType;
import exception.GameServerException;
import message.Message;
import manager.GameRoomManager;
import manager.ConnectionManager;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sender {
    private final GameRoomManager roomManager;
    private final ConnectionManager connectionManager;

    public Sender(GameRoomManager roomManager, ConnectionManager connectionManager) {
        this.roomManager = roomManager;
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
    }

    public void sendToAll(Message message, int roomId) throws GameServerException {
        roomManager.getRoom(roomId).getUserList().forEach(
                user -> {
                    send(message, user.getId());
                }
        );
    }
}
