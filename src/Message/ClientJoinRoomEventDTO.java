package Message;

public class ClientJoinRoomEventDTO extends DTO{

    int roomID;

    public ClientJoinRoomEventDTO(int roomID){

        this.type = MessageType.CLIENT_JOIN_ROOM_EVENT;
        this.roomID = roomID;
    }

    int getRoomID(){ return roomID; }
}
