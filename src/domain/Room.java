package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import exception.ErrorType;
import exception.GameServerException;
import network.Sender;

public class Room {

    private final int id;
    private int drawTimeLimit;
    private int participantLimit;
    private User owner;
    private Vote vote;
    private final List<User> userList = new ArrayList<>();
    private final Map<Integer, Boolean> readyStatusMap = new ConcurrentHashMap<>();

    public Room(int id, int drawTimeLimit, int participantLimit, User owner, Sender sender){
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        this.vote = new Vote(this, sender);
        userList.add(owner);
        readyStatusMap.put(owner.getId(), false);
    }

    public void addUser(User user) throws GameServerException {
        if(userList.size() == participantLimit) {
            throw new GameServerException(ErrorType.ROOM_JOIN_FAILED,
                    String.format("방이 가득 찼습니다. (최대 인원: %d명)", participantLimit));
        }
        userList.add(user);
        readyStatusMap.put(user.getId(), false);
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

    public List<User> getUserList() {
        return userList;
    }

    public boolean isReady(int userId) {
        return readyStatusMap.get(userId);
    }

    public void startVote() throws InterruptedException, GameServerException { vote.startVote(); }

    public void vote(int votedUserID) { vote.vote(votedUserID); }

    public ConcurrentHashMap<Integer, Integer> getVoteState() { return vote.getVoteState(); }

    public boolean isVoteEnd() { return vote.isVoteEnd(); }
}
