package domain;

import dto.event.Event;
import dto.event.server.ServerDrawEvent;
import dto.event.server.ServerFinishGameEvent;
import dto.event.server.ServerStartGameEvent;
import dto.event.server.ServerTurnChangeEvent;
import dto.info.DrawElementInfo;
import exception.ExceptionType;
import exception.GameServerException;
import message.Message;
import util.UnixSeconds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.*;

public class Game {
    private final Room room;
    private final String topic;
    private final int guesserId;
    private final List<Integer> order;
    private final Map<Integer, List<DrawElementInfo>> drawingMap = new ConcurrentHashMap<>();
    private final int timeout;
    private final UnixSeconds gameStartTime = UnixSeconds.now();
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
        Thread thread = new Thread(this::broadCastTurns);
        thread.start();
    }

    private void broadCastGameStartEvent() throws GameServerException {
        Event event = new ServerStartGameEvent(topic, guesserId, order);
        Message message = new Message(SERVER_START_GAME_EVENT, event);
        room.broadcast(message);
    }

    private int getDrawerOfOrder(int i) {
        return order.get(i);
    }

    public void drawBy(int drawer, DrawElementInfo drawing, long submissionTime) throws GameServerException {
        validateSubmissionTime(drawer, UnixSeconds.from(submissionTime));
        List<DrawElementInfo> drawingList = drawingMap.getOrDefault(drawer, new ArrayList<>());
        drawingList.add(drawing);
        drawingMap.put(drawer, drawingList);
        broadCastDrawingEvent(drawer, drawing);
    }

    public void guess(int from, String submittedAnswer) throws GameServerException {
        if (from != guesserId) {
            throw new GameServerException(ExceptionType.GUESS_FROM_NONE_GUESSER);
        }
        this.submittedAnswer = submittedAnswer;
        finishGame();
    }

    private synchronized void validateSubmissionTime(int submitterId, UnixSeconds submissionTime) throws GameServerException {
        int submitterTurnIndex = order.indexOf(submitterId);
        if (submitterTurnIndex == -1) {
            throw new GameServerException(ExceptionType.USER_IS_NOT_IN_ROOM);
        }

        UnixSeconds submitterTurnStart = gameStartTime.plusSeconds((long) submitterTurnIndex * timeout);
        UnixSeconds submitterTurnEnd = submitterTurnStart.plusSeconds(timeout);

        if (submissionTime.isBefore(submitterTurnStart) || submissionTime.isAfter(submitterTurnEnd)) {
            throw new GameServerException(ExceptionType.SUBMISSION_OUT_OF_TIME);
        }
    }

    private void broadCastDrawingEvent(int drawer, DrawElementInfo drawing) throws GameServerException {
        Event event = new ServerDrawEvent(drawer, drawing);
        Message message = new Message(SERVER_DRAW_EVENT, event);
        room.broadcast(message);
    }

    private void broadCastTurns() {
        try {
            for (int i = 0; i < order.size(); i++) {
                broadCastTurnOf(i); // 현재 턴의 브로드캐스트
                Thread.sleep(timeout * 1000L); // 다음 턴까지 대기
            }
        } catch (GameServerException e) {
            room.handleException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            room.handleException(new GameServerException(ExceptionType.SYSTEM_FAILURE, "Game rotation interrupted"));
        }
    }

    private boolean isGuessTurn(int i) {
        return i == order.size() - 1;
    }

    private void broadCastTurnOf(int turnIndex) throws GameServerException {
        UnixSeconds currentTurnStartTime = gameStartTime.plusSeconds((long) timeout * turnIndex);
        int currentDrawer = getDrawerOfOrder(turnIndex); // 현재 턴의 Drawer 계산
        Event event = new ServerTurnChangeEvent(currentDrawer, currentTurnStartTime.toLong(), isGuessTurn(turnIndex));
        Message message = new Message(SERVER_TURN_CHANGE_EVENT, event);
        room.broadcast(message);
    }

    private void finishGame() throws GameServerException {
        broadCastFinish();
    }

    private void broadCastFinish() throws GameServerException {
        Event event = new ServerFinishGameEvent(topic, submittedAnswer, drawingMap);
        Message message = new Message(SERVER_FINISH_GAME_EVENT, event);
        room.broadcast(message);
    }
}
