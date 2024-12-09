package domain;

import dto.event.Event;
import dto.event.server.ServerRoomChatMessage;
import exception.GameServerException;
import message.Message;
import message.MessageType;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private final Room room;

    public Chat(Room room){
        this.room = room;
    }

    public synchronized void sendChat(User from, String content) throws GameServerException {
        Event event = new ServerRoomChatMessage(from.getNickname(), content);
        Message message = new Message(MessageType.SERVER_ROOM_CHAT_MESSAGE, event);
        room.broadcastExcept(message, from);
    }
}
