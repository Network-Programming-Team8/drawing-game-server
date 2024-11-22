package Logic;

import static Message.MessageType.*;
import Message.Message;
import Message.DTO;

public class MessageHandler {

    private GameRoomManager roomManager = null;
    private UserManager userManager = null;

    public MessageHandler(GameRoomManager roomManager, UserManager userManager){

        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    Message handle(Message msg, User from){

        switch(msg.getType()){
            case CLIENT_JOIN_ROOM_EVENT:
                DTO returnDTO = roomManager.getRoom(msg.getMsgDTO());

                return new Message(SERVER_JOIN_ROOM_EVENT, returnDTO);
        }

        return null;
    }

    Message handle(Message msg){

        DTO returnDTO = userManager.createUser(msg.getMsgDTO());

        return new Message(SERVER_LOGIN_EVENT, returnDTO);
    }
}
