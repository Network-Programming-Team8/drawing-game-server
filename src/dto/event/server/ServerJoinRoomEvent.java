package dto.event.server;

import dto.event.Event;
import dto.info.RoomInfo;

import java.io.Serial;

public class ServerJoinRoomEvent extends Event {

    @Serial
    private static final long serialVersionUID = 2491846174256212530L;
    private final RoomInfo roomInfo;

    public ServerJoinRoomEvent(RoomInfo roomInfo){
        this.roomInfo = roomInfo;
    }
}
