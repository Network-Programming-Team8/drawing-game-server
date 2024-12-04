package domain;

import exception.ErrorType;
import exception.GameServerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private final int id;
    private int drawTimeLimit;
    private int participantLimit;
    private User owner;
    private final Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> readyStatusMap = new ConcurrentHashMap<>();

    public Room(int id, int drawTimeLimit, int participantLimit, User owner) throws GameServerException {
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        addUser(owner);
    }

    public void changeSettings(int drawTimeLimit, int participantLimit) {
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
    }

    public void addUser(User user) throws GameServerException {
        if(userMap.size() == participantLimit) {
            throw new GameServerException(ErrorType.ROOM_JOIN_FAILED,
                    String.format("방이 가득 찼습니다. (최대 인원: %d명)", participantLimit));
        }
        userMap.put(user.getId(), user);
        readyStatusMap.put(user.getId(), false);
        user.joinRoom(id);
    }

    public boolean isThereUser(int userId) {
        return userMap.containsKey(userId);
    }

    public void deleteUser(int userId) throws GameServerException {
        if (!isThereUser(userId)) {
            throw new GameServerException(ErrorType.USER_IS_NOT_IN_ROOM);
        }
        User user = userMap.get(userId);
        user.leaveRoom();
        userMap.remove(userId);
        readyStatusMap.remove(userId);
    }

    public int getId() {
        return id;
    }

    public int getDrawTimeLimit() {
        return drawTimeLimit;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public List<Integer> getUserIdList() {
        return new ArrayList<>(userMap.keySet());
    }

    public List<User> getUserList() {
        return userMap.values().stream().toList();
    }

    public boolean isReady(int userId) {
        return readyStatusMap.get(userId);
    }
}
