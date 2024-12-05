package domain;

import dto.event.Event;
import dto.event.server.ServerDrawEvent;
import dto.event.server.ServerStartGameEvent;
import dto.info.DrawElementInfo;
import exception.ErrorType;
import exception.GameServerException;
import message.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static message.MessageType.SERVER_DRAW_EVENT;
import static message.MessageType.SERVER_START_GAME_EVENT;

public class Game {
    private final Room room;
    private final String topic;
    private final int guesserId;
    private final List<Integer> order;
    private final Map<Integer, List<DrawElementInfo>> drawingMap = new ConcurrentHashMap<>();
    private int currentOrder = 0;

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

    private int getCurrentDrawer() {
        return order.get(currentOrder);
    }

    public void drawBy(int drawer, DrawElementInfo drawing) throws GameServerException {
        if(getCurrentDrawer() != drawer) {
            throw new GameServerException(ErrorType.DRAWER_OUT_OF_ORDER);
        }
        List<DrawElementInfo> drawingList = drawingMap.getOrDefault(drawer, List.of());
        drawingList.add(drawing);
        drawingMap.put(drawer, drawingList);
        broadCastDrawingEvent(drawer, drawing);
    }

    private void broadCastDrawingEvent(int drawer, DrawElementInfo drawing) throws GameServerException {
        Event event = new ServerDrawEvent(drawer, drawing);
        Message message = new Message(SERVER_DRAW_EVENT, event);
        room.broadcastTo(message, order.subList(0, currentOrder));
    }
}
