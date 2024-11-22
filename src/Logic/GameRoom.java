package Logic;

import java.util.List;

public class GameRoom {

    int id;
    int drawTimeLimit;
    int participantLimit;
    List<User> userList;

    public GameRoom(int drawTimeLimit, int participantLimit){

        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
    }

}
