package manager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import network.Connection;
import domain.User;
import exception.GameServerException;

public class ConnectionManager {

    private final GameRoomManager gameRoomManager;
    private final Map<Integer, Connection> connectionMap = new ConcurrentHashMap<>();
    private int lastID = 0;

    public ConnectionManager(GameRoomManager gameRoomManager) {
        this.gameRoomManager = gameRoomManager;
    }

    private Connection getConnection (int id) {
        if(!hasConnection(id)) {
            System.err.println("연결을 찾을 수 없습니다.");
        }
        return connectionMap.get(id);
    }

    public boolean hasConnection(int id) {
        return connectionMap.containsKey(id);
    }

    public int addConnection (ObjectOutputStream os, ObjectInputStream is) {
        connectionMap.put(++lastID, new Connection(os, is));
        return lastID;
    }

    public boolean hasUser(int id) {
        return getConnection(id).hasUser();
    }

    public User getUser(int id) throws GameServerException {
        return getConnection(id).getUser();
    }

    public ObjectOutputStream getOutputStream(int id) {
        return getConnection(id).getOs();
    }

    public ObjectInputStream getInputStream(int id) {
        return getConnection(id).getIs();
    }

    public void registerUserTo(User user, int id) {
        getConnection(id).registerUser(user);
    }

    public void closeConnection (int id) {
        try {
            if(hasConnection(id) && hasUser(id)) {
                User user = getUser(id);
                if(user.isInRoom()) {
                    gameRoomManager.deleteUserFrom(id, user.getRoomId());
                }
            }
            getConnection(id).close();
            connectionMap.remove(id);
        } catch (GameServerException e) {
            System.err.println("연결 종료 중 에러 발생");
        }
    }
}