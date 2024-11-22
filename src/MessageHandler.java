public class MessageHandler {
    private final GameRoom gameRoom;
    void handle(Message msg, User from){
        switch(msg.getType()):

        case CLIENT_JOINT_ROOM_EVENT:
            ClientJoinRoomEvent clientJoinRoomEvent = (ClientJoinRoomEvent) msg.dto;
            gameRoom = gameRoom.getRoom(clientJoinRoomEvent.roomId);
            gameRoom.join(from);

    }

    void handle()
}
