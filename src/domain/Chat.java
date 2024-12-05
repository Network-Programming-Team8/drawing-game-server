package domain;

import dto.event.Event;
import dto.event.server.ServerRoomChatMessage;
import exception.GameServerException;
import message.Message;
import message.MessageType;

public class Chat {

    private final Room room;

    public Chat(Room room){
        this.room = room;
    }

    public void chatting(String from, String content) throws GameServerException {
        Event event = new ServerRoomChatMessage(from, content);
        Message message = new Message(MessageType.SERVER_ROOM_CHAT_MESSAGE, event);
        room.broadcast(message);
    }
}
