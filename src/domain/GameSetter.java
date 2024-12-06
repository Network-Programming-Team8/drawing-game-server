package domain;

import dto.event.Event;
import dto.event.server.ServerRequestTopicEvent;
import exception.ErrorType;
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

    private Game setGame() throws GameServerException {
        String topic = selectTopic();
        List<Integer> order = setOrder();
        int guesserId = order.get(order.size() - 1);
        return new Game(room, topic, guesserId, order, room.getDrawTimeLimit());
    }

    private String selectTopic() {
        // 모든 제안된 토픽을 리스트로 변환
        List<String> topics = new ArrayList<>(topicSuggestionMap.values());
        // 무작위로 토픽 선택
        Collections.shuffle(topics);
        return topics.get(0);
    }

    private int selectGuesser() {
        // 선택된 토픽
        String selectedTopic = selectTopic();
        // 선택된 토픽을 제안하지 않은 유저 ID 목록 생성
        List<Integer> eligibleGuessers = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : topicSuggestionMap.entrySet()) {
            if (!entry.getValue().equals(selectedTopic)) {
                eligibleGuessers.add(entry.getKey());
            }
        }
        // 무작위로 guesser 선택
        Collections.shuffle(eligibleGuessers);
        return eligibleGuessers.get(0);
    }

    private List<Integer> setOrder() {
        // 방에 있는 모든 유저의 ID 가져오기
        List<Integer> userIds = new ArrayList<>(room.getUserIdList());
        // 추측자(guesser) ID 가져오기
        int guesserId = selectGuesser();

        // 추측자를 제외하고 섞기
        userIds.remove((Integer) guesserId);
        Collections.shuffle(userIds);

        // 추측자를 리스트의 마지막에 추가
        userIds.add(guesserId);
        return userIds;
    }

    public Game getGame() throws GameServerException {
        if (game == null) {
            throw new GameServerException(ErrorType.NO_GAME_RUNNING);
        }
        return game;
    }
}
