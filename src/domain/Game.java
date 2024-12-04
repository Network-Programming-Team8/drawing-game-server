package domain;

import dto.event.Event;
import dto.event.server.ServerStartGameEvent;
import exception.GameServerException;
import message.Message;
import network.Sender;

import java.util.List;

import static message.MessageType.SERVER_ROOM_UPDATE_EVENT;

public class Game {
    private final Room room;
    private final String topic;
    private final int guesserId;
    private final List<Integer> order;
    private final Sender sender;

    public Game(Room room, String topic, int guesserId, List<Integer> order, Sender sender) {
        this.room = room;
        this.topic = topic;
        this.guesserId = guesserId;
        this.order = order;
        this.sender = sender;
    }

    public void startGame() throws GameServerException {
        broadCastGameStartEvent();
    }

    private void broadCastGameStartEvent() throws GameServerException {
        Event event = new ServerStartGameEvent(topic, guesserId, order);
        Message message = new Message(SERVER_ROOM_UPDATE_EVENT, event);
        broadcastIn(message, room);
    }

    //TODO ServerEventListener 만들어서 Sender 대신 주입, 아래 중복 책임 담당하도록 바꾸기
    private void sendTo(Message message, User to) {
        sender.send(message, to.getId());
    }
    private void broadcastIn(Message message, Room room) throws GameServerException {
        sender.sendToAll(message, room.getUserList().stream().map(User::getId).toList());
    }
}
