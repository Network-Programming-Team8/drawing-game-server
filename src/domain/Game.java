package domain;

import dto.event.Event;
import dto.event.server.ServerStartGameEvent;
import exception.GameServerException;
import message.Message;

import java.util.List;

import static message.MessageType.SERVER_START_GAME_EVENT;

public class Game {
    private final Room room;
    private final String topic;
    private final int guesserId;
    private final List<Integer> order;

    public Game(Room room, String topic, int guesserId, List<Integer> order) {
        this.room = room;
        this.topic = topic;
        this.guesserId = guesserId;
        this.order = order;
    }

    public void startGame() throws GameServerException {
        broadCastGameStartEvent();
    }

    private void broadCastGameStartEvent() throws GameServerException {
        Event event = new ServerStartGameEvent(topic, guesserId, order);
        Message message = new Message(SERVER_START_GAME_EVENT, event);
        room.broadcast(message);
    }
}
