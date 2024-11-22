import Message.Message;
import Message.DTO;


public class MessageHandler {

    private GameRoomManager roomManager = null;
    private UserManager userManager = null;
    private GameRoom gameRoom = null;

    public MessageHandler(GameRoomManager roomManager, UserManager userManager){

        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    void handle(Message msg, User from){

        switch(msg.getType()){
            case CLIENT_JOIN_ROOM_EVENT:
                break;

            case SERVER_JOIN_ROOM_EVENT:
                break;

        }
    }

    String handle(Message msg){

        return null;
    }
}
