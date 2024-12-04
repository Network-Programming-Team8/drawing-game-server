package manager;

import java.util.HashMap;
import java.util.Map;
import domain.User;
import domain.Room;
import exception.GameServerException;
import exception.ErrorType;

public class GameRoomManager {
    private final Map<Integer, Room> gameRoomList = new HashMap<>();
    private int lastID = 0;

    public Room createRoom(int drawTimeLimit, int participantLimit, User creator) throws GameServerException {
        try {
            Room newRoom = new Room(++lastID, drawTimeLimit, participantLimit, creator);
            gameRoomList.put(lastID, newRoom);
            return newRoom;
        } catch (Exception e) {
            throw new GameServerException(ErrorType.ROOM_CREATION_FAILED, e);
        }
    }

    public Room getRoom(int roomID) throws GameServerException {
        if (!gameRoomList.containsKey(roomID)) {
            throw new GameServerException(ErrorType.ROOM_NOT_FOUND);
        }
        return gameRoomList.get(roomID);
    }

    public void deleteRoom(int roomID) throws GameServerException {
        if (!gameRoomList.containsKey(roomID)) {
            throw new GameServerException(ErrorType.ROOM_NOT_FOUND);
        }
        gameRoomList.remove(roomID);
    }
}
