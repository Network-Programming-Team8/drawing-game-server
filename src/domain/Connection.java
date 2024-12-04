package domain;

import exception.ErrorType;
import exception.GameServerException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Connection {
    private final ObjectOutputStream os;
    private final ObjectInputStream is;
    private User user = null;

    public Connection(ObjectOutputStream os, ObjectInputStream is) {
        this.os = os;
        this.is = is;
    }

    public void registerUser(User user) {
        this.user = user;
    }

    public User getUser() throws GameServerException {
        if(user == null) {
            throw new GameServerException(ErrorType.UNKNOWN_ERROR);
        }
        return user;
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    public ObjectInputStream getIs() {
        return is;
    }
}
