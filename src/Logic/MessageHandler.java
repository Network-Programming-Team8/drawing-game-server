package Logic;

import static message.MessageType.*;

import exception.GameServerException;
import message.Message;
import dto.DTO;
import domain.User;

public class MessageHandler {

    private final GameRoomManager roomManager;
    private final UserManager userManager;

    public MessageHandler(GameRoomManager roomManager, UserManager userManager){
        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    public Message handle(Message msg, User from) throws GameServerException {
        Message response = null;
        switch(msg.getType()){
            case CLIENT_JOIN_ROOM_EVENT:
                DTO returnDTO = roomManager.getRoom(msg.getMsgDTO());
                response = new Message(SERVER_JOIN_ROOM_EVENT, returnDTO);
        }
        return response;
    }
}