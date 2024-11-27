package dto.event.server;

import dto.event.Event;
import dto.info.RoomInfo;
import dto.info.UserInfo;

import java.util.List;

public class ServerJoinRoomEventDTO extends Event {
    private final RoomInfo roomInfo;

    public ServerJoinRoomEventDTO(RoomInfo roomInfo){
        this.roomInfo = roomInfo;
    }
}
