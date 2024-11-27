package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    int id;
    int drawTimeLimit;
    int participantLimit;
    User owner;
    List<User> userList = new ArrayList<User>();
    Map<Integer, Boolean> readyStatusMap = new HashMap<>();

    public Room(int id, int drawTimeLimit, int participantLimit, User owner){
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.owner = owner;
        userList.add(owner);
        readyStatusMap.put(owner.getId(), false);
    }

    public void addUser(User user) {
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
}
