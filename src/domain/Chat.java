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

    public void chatting(User from, String content) throws GameServerException {
        List<Integer> userList = room.getUserIdList();
        List<Integer> sendList = new ArrayList<>();
        for (int user : userList)
            if (user != from.getId())
                sendList.add(user);

        Event event = new ServerRoomChatMessage(from.getNickname(), content);
        Message message = new Message(MessageType.SERVER_ROOM_CHAT_MESSAGE, event);
        room.broadcastTo(message, sendList);
    }
}
