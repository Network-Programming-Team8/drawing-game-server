package service;

import java.util.Map;

import domain.User;
import domain.GameRoom;

public class GameRoomManager {

    private Map<Integer, GameRoom> gameRoomList;
    private int lastID = 0;

    public GameRoom createRoom(int drawTimeLimit, int participantLimit, User creator){
        GameRoom newRoom = new GameRoom(++lastID, drawTimeLimit, participantLimit, creator);
        gameRoomList.put(lastID, newRoom);
        return newRoom;
    }

    public GameRoom getRoom(int roomID){
        return gameRoomList.get(roomID);
    }

    public void deleteRoom(int roomID){
        gameRoomList.remove(roomID);
    }
}
