package service;

import java.util.Map;

import domain.User;
import domain.GameRoom;
import exception.GameServerException;

public class GameRoomManager {
    static final String ROOM_NOT_FOUND_ERROR = "해당 ID의 방이 존재하지 않습니다.";

    private Map<Integer, GameRoom> gameRoomList;
    private int lastID = 0;

    public GameRoom createRoom(int drawTimeLimit, int participantLimit, User creator) throws GameServerException {
        try {
            GameRoom newRoom = new GameRoom(++lastID, drawTimeLimit, participantLimit, creator);
            gameRoomList.put(lastID, newRoom);
            return newRoom;
        } catch (Exception e) {
            throw new GameServerException("방 생성 실패: 시스템 문제", e);
        }
    }

    public GameRoom getRoom(int roomID) throws GameServerException {
        if(!gameRoomList.containsKey(roomID)) {
            throw new GameServerException(ROOM_NOT_FOUND_ERROR);
        }
        return gameRoomList.get(roomID);
    }

    public void deleteRoom(int roomID) throws GameServerException {
        if(!gameRoomList.containsKey(roomID)) {
            throw new GameServerException(ROOM_NOT_FOUND_ERROR);
        }
        gameRoomList.remove(roomID);
    }
}
