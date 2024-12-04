package network;

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

    public void send(Message message, ObjectOutputStream os) {
        try {
            os.writeObject(message);
        } catch (IOException e) {
            System.err.println("송신 중 오류 발생");
            throw new RuntimeException(e); //아래 send에서도 알아야해서 다른 exception으로 해야 함
        }
    }

    public void send(Message message, int userId) {
        send(message, connectionManager.getOutputStream(userId));
    }

    public void sendToAll(Message message, int roomId) throws GameServerException {
        roomManager.getRoom(roomId).getUserList().forEach(
            user -> { send(message, user.getId()); }
        );
    }
}