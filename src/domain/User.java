package domain;

import exception.ErrorType;
import exception.GameServerException;

public class User {

    private final int id;
    private final String nickname;
    private Integer roomID;

    public User(int id, String nickname){
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public boolean isInRoom() {
        return roomID != null;
    }

    public int getRoomId() throws GameServerException {
        if( !isInRoom() ) {
            throw new GameServerException(ErrorType.USER_IS_NOT_IN_ROOM);
        }
        return roomID;
    }

    public void joinRoom(int roomId) throws GameServerException{
        if( isInRoom() ) {
            throw new GameServerException(ErrorType.USER_ALREADY_IN_ROOM);
        }
        this.roomID = roomId;
    }

    public void leaveRoom() throws GameServerException {
        if( !isInRoom() ) {
            throw new GameServerException(ErrorType.USER_IS_NOT_IN_ROOM);
        }
        roomID = null;
    }

    public String getNickname() {
        return nickname;
    }
}
