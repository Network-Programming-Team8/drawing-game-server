package domain;

import dto.event.Event;
import dto.event.server.ServerRoomUpdateEvent;
import exception.ErrorType;
import exception.ExceptionHandler;
import exception.GameServerException;
import mapper.RoomMapper;
import message.Message;
import network.Sender;
import util.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.*;

public class Room {
    private final int id;
    private int drawTimeLimit;
    private int participantLimit;
    private User owner;
    private final Vote vote;
    private final Chat chat;
    private final Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> readyStatusMap = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> voteReadyStatusMap = new ConcurrentHashMap<>();
    private final Sender sender;
    private final GameSetter gameSetter;
    private final ExceptionHandler exceptionHandler;

    public Room(int id, int drawTimeLimit, int participantLimit, User owner, Sender sender,
                ExceptionHandler exceptionHandler1) throws GameServerException {
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        this.sender = sender;
        this.exceptionHandler = exceptionHandler1;
        this.vote = new Vote(this);
        this.chat = new Chat(this);
        this.gameSetter = new GameSetter(this);
        addUser(owner);
    }

    public void changeSettings(int drawTimeLimit, int participantLimit) {
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
    }

    public void addUser(User user) throws GameServerException {
        if(userMap.size() == participantLimit) {
            throw new GameServerException(ErrorType.ROOM_JOIN_FAILED,
                    String.format("Room is full (max: %d)", participantLimit));
        }
        userMap.put(user.getId(), user);
        readyStatusMap.put(user.getId(), false);
        user.joinRoom(id);
    }

    public boolean isThereUser(int userId) {
        return userMap.containsKey(userId);
    }

    public synchronized void deleteUser(int userId) throws GameServerException {
        if (!isThereUser(userId)) {
            throw new GameServerException(ErrorType.USER_IS_NOT_IN_ROOM);
        }
        User user = userMap.get(userId);
        try {
            if (user.equals(owner)) {
                setNewRandomOwner();
            }
        } catch (GameServerException e) {
            if(!e.getErrorType().equals(ErrorType.OWNER_SELECT_FAILED)) {
                throw e;
            }
        }
        user.leaveRoom();
        userMap.remove(userId);
        readyStatusMap.remove(userId);
        broadCastRoomUpdateEvent();
    }

    private synchronized void setNewRandomOwner() throws GameServerException {
        if(userMap.size() == 1) {
            throw new GameServerException(ErrorType.OWNER_SELECT_FAILED);
        }
        owner = Utils.selectRandomlyFrom(
                userMap.values().stream()
                        .filter(u -> !u.equals(owner)
                        ).toList());
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

    public void setReady(int userId, boolean ready) {
        readyStatusMap.put(userId, ready);
    }

    public void setVoteReady(int userId, boolean ready) {
        voteReadyStatusMap.put(userId,ready);
    }

    public void tryToStartGame() throws GameServerException {
        if(readyStatusMap.size() == participantLimit &&
                readyStatusMap.values().stream().allMatch(readyStatus -> readyStatus)) {
            gameSetter.requestTopic();
        }
    }

    public void tryToStartVote() throws GameServerException {
        if(voteReadyStatusMap.size() == participantLimit &&
                voteReadyStatusMap.values().stream().allMatch(readyStatus -> readyStatus)){
            startVote();
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

    void broadcast(Message message) throws GameServerException {
        broadcastTo(message, this.getUserList().stream().map(User::getId).toList());
    }
    void broadcastExcept(Message message, User except) throws GameServerException {
        broadcastTo(message, this.getUserList().stream().filter(user -> !user.equals(except)).map(User::getId).toList());
    }
    void broadcastTo(Message message, List<Integer> idList) throws GameServerException {
        if( idList.stream().allMatch(this::isThereUser)) {
            sender.sendToAll(message, idList);
            return;
        }
        throw new GameServerException(ErrorType.USER_IS_NOT_IN_ROOM);
    }

    public void broadCastRoomUpdateEvent() throws GameServerException {
        Event event = new ServerRoomUpdateEvent(RoomMapper.toRoomInfo(this));
        Message message = new Message(SERVER_ROOM_UPDATE_EVENT, event);
        broadcast(message);
    }

    public void sendChat(User from, String content) throws GameServerException { chat.sendChat(from, content); }

    private void startVote() throws GameServerException { vote.startVote(); }

    public void vote(int to, int from) throws GameServerException {
        vote.vote(to, from);
    }

    public Game getGameOnPlay() throws GameServerException {
        return gameSetter.getGame();
    }

    public boolean canChangeSettings(User from) {
        return owner.equals(from);
    }

    public int getOwnerId() {
        return owner.getId();
    }

    public void handleException(Exception e) {
        exceptionHandler.handle(e, getUserIdList());
    }
}
