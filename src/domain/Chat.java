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
    private final List<Integer> userList;

    public Chat(Room room){
        this.room = room;
        this.userList = room.getUserIdList();
    }

    public void chatting(User from, String content) throws GameServerException {
        Event event = new ServerRoomChatMessage(from.getNickname(), content);
        Message message = new Message(MessageType.SERVER_ROOM_CHAT_MESSAGE, event);
        List<Integer> sendList = new ArrayList<>();

        for (Integer user : userList) {
            if (user != from.getId())
                sendList.add(user);
        }

        room.broadcastTo(message, sendList);
    }
}