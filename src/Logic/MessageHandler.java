package Logic;

import static message.MessageType.*;
import message.Message;
import dto.DTO;
import domain.User;

public class MessageHandler {

    private GameRoomManager roomManager = null;
    private UserManager userManager = null;

    public MessageHandler(GameRoomManager roomManager, UserManager userManager){

        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    public Message handle(Message msg, User from){

        switch(msg.getType()){
            case CLIENT_JOIN_ROOM_EVENT:
                DTO returnDTO = roomManager.getRoom(msg.getMsgDTO());

                return new Message(SERVER_JOIN_ROOM_EVENT, returnDTO);
        }

        return null;
    }

    public Message handle(Message msg){

        DTO returnDTO = userManager.createUser(msg.getMsgDTO());

        return new Message(SERVER_LOGIN_EVENT, returnDTO);
    }
}
