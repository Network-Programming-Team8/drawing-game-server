package manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import domain.User;
import domain.Room;
import domain.Vote;
import exception.GameServerException;
import exception.ErrorType;
import network.Sender;

public class GameRoomManager {
    private final Map<Integer, Room> gameRoomList = new ConcurrentHashMap<>();
    private int lastID = 0;

    public Room createRoom(int drawTimeLimit, int participantLimit, User creator, Sender sender) throws GameServerException {
        try {
            Room newRoom = new Room(++lastID, drawTimeLimit, participantLimit, creator);
            Vote vote = new Vote(newRoom, sender);
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
