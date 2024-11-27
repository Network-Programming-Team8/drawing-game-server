package dto.event.client;

import dto.event.Event;

public class ClientJoinRoomEventDTO extends Event {

    int roomID;

    public ClientJoinRoomEventDTO(int roomID){
        this.roomID = roomID;
    }

    public int getRoomID(){ return roomID; }
}
