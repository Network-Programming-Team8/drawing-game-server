package domain;

import exception.ErrorType;
import exception.GameServerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private final int id;
    private final int drawTimeLimit;
    int participantLimit;
    private User owner;
    private final List<User> userList = new ArrayList<>();
    private final Map<Integer, Boolean> readyStatusMap = new HashMap<>();
    private final Map<Integer, Integer> voteCounter = new HashMap<>();
    private boolean isGameEnd;
    private boolean isVoteEnd;

    public Room(int id, int drawTimeLimit, int participantLimit, User owner){
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        userList.add(owner);
        readyStatusMap.put(owner.getId(), false);
        this.isGameEnd = false;
        this.isVoteEnd = false;
    }

    public void addUser(User user) throws GameServerException {
        if(userList.size() == participantLimit) {
            throw new GameServerException(ErrorType.ROOM_JOIN_FAILED,
                    String.format("방이 가득 찼습니다. (최대 인원: %d명)", participantLimit));
        }
        userList.add(user);
        readyStatusMap.put(user.getId(), false);
    }

    public void vote(int id) {
        if(voteCounter.containsKey(id)){
            int numOfVote = voteCounter.get(id);
            voteCounter.remove(id);
            voteCounter.put(id, numOfVote+1);
        }
        else{
            voteCounter.put(id, 1);
        }
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

    public boolean getIsGameEnd() { return isGameEnd; }

    public void setIsVoteEnd() { isVoteEnd = true; }

    public boolean getIsVoteEnd() { return isVoteEnd; }

    public Map<Integer, Integer> getVoteState(){ return voteCounter; }
}
