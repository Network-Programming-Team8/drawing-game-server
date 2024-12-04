package manager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import domain.Connection;
import domain.User;
import exception.ErrorType;
import exception.GameServerException;

public class ConnectionManager {

    private Map<Integer, Connection> connectionMap = new HashMap<>();
    private int lastID = 0;

    private Connection getConnection (int id) {
        if(!connectionMap.containsKey(id)) {
            System.err.println("연결을 찾을 수 없습니다.");
        }
        return connectionMap.get(id);
    }

    public int addConnection (ObjectOutputStream os, ObjectInputStream is) {
        connectionMap.put(++lastID, new Connection(os, is));
        return lastID;
    }

    public User getUser(int id) throws GameServerException {
        return getConnection(id).getUser();
    }

    public ObjectOutputStream getOutputStream(int id) {
        return getConnection(id).getOs();
    }

    public void registerUserTo(User user, int id) {
        getConnection(id).registerUser(user);
    }

    public void deleteConnection (int id) {
        connectionMap.remove(id);
    }
}