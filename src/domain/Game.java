package domain;

import dto.event.Event;
import dto.event.server.ServerDrawEvent;
import dto.event.server.ServerFinishGameEvent;
import dto.event.server.ServerStartGameEvent;
import dto.event.server.ServerTurnChangeEvent;
import dto.info.DrawElementInfo;
import exception.ErrorType;
import exception.GameServerException;
import message.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Game {
    private final Room room;
    private final String topic;
    private final int guesserId;
    private final List<Integer> order;
    private final Map<Integer, List<DrawElementInfo>> drawingMap = new ConcurrentHashMap<>();
    private int currentOrder = 0;
    private final int timeout;
    private final AtomicReference<LocalDateTime> currentTurnStartTime = new AtomicReference<>();
    private String submittedAnswer = "";

    public Game(Room room, String topic, int guesserId, List<Integer> order, int timeout) {
        this.room = room;
        this.topic = topic;
        this.guesserId = guesserId;
        this.order = order;
        this.timeout = timeout;
    }

    public void startGame() throws GameServerException {
        broadCastGameStartEvent();
        rotateTurns();
        //broadCastFinish();
    }

    private void broadCastGameStartEvent() throws GameServerException {
        Event event = new ServerStartGameEvent(topic, guesserId, order);
        Message message = new Message(SERVER_START_GAME_EVENT, event);
        room.broadcast(message);
    }

    private int getCurrentDrawer() {
        return order.get(currentOrder);
    }

    public void drawBy(int drawer, DrawElementInfo drawing, LocalDateTime submissionTime) throws GameServerException {
        validateSubmissionTime(submissionTime);
        if (getCurrentDrawer() != drawer) {
            throw new GameServerException(ErrorType.DRAWER_OUT_OF_ORDER);
        }
        List<DrawElementInfo> drawingList = drawingMap.getOrDefault(drawer, List.of());
        drawingList.add(drawing);
        drawingMap.put(drawer, drawingList);
        broadCastDrawingEvent(drawer, drawing);
    }

    public void guess(int from, String submittedAnswer, LocalDateTime submissionTime) throws GameServerException {
        validateSubmissionTime(submissionTime);
        if (from != guesserId) {
            throw new GameServerException(ErrorType.GUESS_FROM_NONE_GUESSER);
        }
        this.submittedAnswer = submittedAnswer;
    }

    private void validateSubmissionTime(LocalDateTime submissionTime) throws GameServerException {
        LocalDateTime turnStart = currentTurnStartTime.get();
        if (turnStart == null || submissionTime.isBefore(turnStart) ||
                submissionTime.isAfter(turnStart.plusSeconds(timeout))) {
            throw new GameServerException(ErrorType.SUBMISSION_OUT_OF_TIME);
        }
    }

    private void broadCastDrawingEvent(int drawer, DrawElementInfo drawing) throws GameServerException {
        Event event = new ServerDrawEvent(drawer, drawing);
        Message message = new Message(SERVER_DRAW_EVENT, event);
        room.broadcastTo(message, order.subList(0, currentOrder));
    }

    private void rotateTurns() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        for (currentOrder = 0; currentOrder < order.size(); currentOrder++) {
            scheduler.schedule(() -> {
                try {
                    changeTurn();
                } catch (GameServerException e) {
                    //TODO 서버이벤트리스너 추가 후에 처리하기
                    throw new RuntimeException(e);
                }
            }, timeout, TimeUnit.SECONDS);
        }
        scheduler.shutdown(); // 타이머 종료
    }

    private void changeTurn() throws GameServerException {
        currentTurnStartTime.set(LocalDateTime.now());
        broadCastCurrentTurn();
    }

    private void broadCastCurrentTurn() throws GameServerException {
        int currentDrawer = getCurrentDrawer();
        boolean isGuessTurn = (currentDrawer == guesserId);
        Event event = new ServerTurnChangeEvent(currentDrawer, currentTurnStartTime.get(), isGuessTurn);
        Message message = new Message(SERVER_TURN_CHANGE_EVENT, event);
        room.broadcast(message);
    }

    private void broadCastFinish() throws GameServerException {
        Event event = new ServerFinishGameEvent(topic, submittedAnswer, drawingMap);
        Message message = new Message(SERVER_FINISH_GAME_EVENT, event);
        room.broadcast(message);
    }
}
