package manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import domain.User;
import domain.Room;
import exception.ExceptionHandler;
import exception.GameServerException;
import exception.ExceptionType;
import network.Sender;

public class GameRoomManager {
    private final Map<Integer, Room> gameRoomMap = new ConcurrentHashMap<>();
    private int lastID = 0;

    public Room createRoom(int drawTimeLimit, int participantLimit, User creator, Sender sender, ExceptionHandler exceptionHandler) throws GameServerException {
        try {
            Room newRoom = new Room(++lastID, drawTimeLimit, participantLimit, creator, sender, exceptionHandler);
            gameRoomMap.put(lastID, newRoom);
            return newRoom;
        } catch (Exception e) {
            throw new GameServerException(ExceptionType.ROOM_CREATION_FAILED, e);
        }
    }

    public Room getRoom(int roomID) throws GameServerException {
        if (!gameRoomMap.containsKey(roomID)) {
            throw new GameServerException(ExceptionType.ROOM_NOT_FOUND);
        }
        return gameRoomMap.get(roomID);
    }

    public void deleteRoom(int roomID) throws GameServerException {
        if (!gameRoomMap.containsKey(roomID)) {
            throw new GameServerException(ExceptionType.ROOM_NOT_FOUND);
        }
        gameRoomMap.remove(roomID);
    }

    public void deleteUserFrom(int userId, int roomId) throws GameServerException {
        Room room = getRoom(roomId);
        room.deleteUser(userId);
        if(room.isEmpty()) {
            deleteRoom(roomId);
        }
    }
}
