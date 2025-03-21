package domain;

import dto.event.Event;
import dto.event.server.ServerRequestTopicEvent;
import exception.ExceptionType;
import exception.GameServerException;
import message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.SERVER_REQUEST_TOPIC_EVENT;

public class GameSetter {
    private final Map<Integer, String> topicSuggestionMap = new ConcurrentHashMap<>();
    private final Room room;
    private Game game;

    public GameSetter(Room room) {
        this.room = room;
    }

    public void requestTopic() throws GameServerException {
        Event event = new ServerRequestTopicEvent();
        Message message = new Message(SERVER_REQUEST_TOPIC_EVENT, event);
        room.broadcast(message);
    }

    public void getSuggestion(String topic, int id) throws GameServerException {
        topicSuggestionMap.put(id, topic);
        if (topicSuggestionMap.size() == room.getSize()) {
            //TODO limit으로할지 실제 현재 명수로 할지? 누가 ready 누르고 나가면?
            //일단은 한 번 시작했으면 진행하기로 했으니까 실제 명수로 합시다
            game = setGame();
            game.startGame();

        }
    }

    private Game setGame() {
        List<Integer> userIds = new ArrayList<>(room.getUserIdList());
        Collections.shuffle(userIds);
        String topic = topicSuggestionMap.get(userIds.get(0));
        int guesserId = userIds.get(userIds.size() - 1);
        return new Game(room, topic, guesserId, userIds, room.getDrawTimeLimit());
    }

    public Game getGame() throws GameServerException {
        if (game == null) {
            throw new GameServerException(ExceptionType.NO_GAME_RUNNING);
        }
        return game;
    }
}
