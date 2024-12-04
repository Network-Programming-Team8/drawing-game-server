package domain;

import dto.event.Event;
import dto.event.server.ServerRoomUpdateEvent;
import exception.ErrorType;
import exception.GameServerException;
import mapper.RoomMapper;
import message.Message;
import network.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.SERVER_ROOM_UPDATE_EVENT;

public class Room {
    private final int id;
    private int drawTimeLimit;
    private int participantLimit;
    private User owner;
    private final Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> readyStatusMap = new ConcurrentHashMap<>();
    private final Sender sender;
    private final GameSetter gameSetter = new GameSetter(this);

    public Room(int id, int drawTimeLimit, int participantLimit, User owner, Sender sender) throws GameServerException {
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        this.sender = sender;
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
        broadCastRoomUpdateEvent();
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

    public void setReady(int userId, boolean ready) throws GameServerException {
        readyStatusMap.put(userId, ready);
        if(readyStatusMap.size() == participantLimit &&
                readyStatusMap.values().stream().allMatch(readyStatus -> readyStatus)) {
            gameSetter.requestTopic();
        }
    }

    public void getSuggestion(String topic, int id) throws GameServerException {
        gameSetter.getSuggestion(topic, id);
    }

    public boolean isEmpty() {
        return userMap.isEmpty();
    }

    public int getSize() {
        return userMap.size();
    }

    //TODO ServerEventListener 만들어서 Sender 대신 주입, 아래 중복 책임 담당하도록 바꾸기
    private void sendTo(Message message, User to) {
        sender.send(message, to.getId());
    }
    void broadcast(Message message) throws GameServerException {
        sender.sendToAll(message, this.getUserList().stream().map(User::getId).toList());
    }
    private void broadCastRoomUpdateEvent() throws GameServerException {
        Event event = new ServerRoomUpdateEvent(RoomMapper.toRoomInfo(this));
        Message message = new Message(SERVER_ROOM_UPDATE_EVENT, event);
        broadcast(message);
    }
}
