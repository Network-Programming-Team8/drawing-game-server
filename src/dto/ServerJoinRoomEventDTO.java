package dto;

import domain.User;

import java.util.List;

public class ServerJoinRoomEventDTO extends DTO{
    int id;
    int drawTimeLimit;
    int participantLimit;
    List<UserInfo> userList;

    public ServerJoinRoomEventDTO(int id, int drawTimeLimit, int participantLimit, List<UserInfo> userList){
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.userList = userList;
    }
}
