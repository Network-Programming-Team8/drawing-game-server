package manager;

import domain.Game;
import domain.Room;
import domain.User;
import dto.event.client.*;
import exception.ExceptionHandler;
import exception.ExceptionType;
import exception.GameServerException;
import message.Message;
import message.MessageHandler;
import message.MessageType;
import network.Sender;

import java.util.HashMap;
import java.util.Map;
import static message.MessageType.*;

public class MessageHandlerManager {
    private final GameRoomManager roomManager;
    private final Sender sender;
    private final ExceptionHandler exceptionHandler;
    private final Map<MessageType, MessageHandler> handlers = new HashMap<>();

    public MessageHandlerManager(GameRoomManager roomManager, Sender sender, ExceptionHandler exceptionHandler) {
        this.roomManager = roomManager;
        this.sender = sender;
        this.exceptionHandler = exceptionHandler;
        initializeHandlers();
    }

    private void initializeHandlers() {
        handlers.put(CLIENT_CREATE_ROOM_EVENT, (msg, user) -> handleCreateRoomEvent((ClientCreateRoomEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_JOIN_ROOM_EVENT, (msg, user) -> handleJoinRoomEvent((ClientJoinRoomEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_CHANGE_ROOM_SETTING_EVENT, (msg, user) -> handleChangeRoomEvent((ClientChangeRoomSettingEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_GAME_READY_EVENT, (msg, user) -> handleGameReadyEvent((ClientReadyEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_EXIT_ROOM_EVENT, (msg, user) -> handleExitRoomEvent((ClientExitRoomEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_ROOM_CHAT_MESSAGE, (msg, user) -> handleRoomChatMessage((ClientRoomChatMessage) msg.getMsgDTO(), user));
        handlers.put(CLIENT_SUGGEST_TOPIC_EVENT, (msg, user) -> handleSuggestTopicEvent((ClientSuggestTopicEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_DRAW_EVENT, (msg, user) -> handleDrawEvent((ClientDrawEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_GUESS_EVENT, (msg, user) -> handleGuessEvent((ClientGuessEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_VOTE_READY_EVENT, (msg, user) -> handleVoteReadyEvent((ClientReadyEvent) msg.getMsgDTO(), user));
        handlers.put(CLIENT_VOTE_EVENT, (msg, user) -> handleVoteEvent((ClientVoteEvent) msg.getMsgDTO(), user));
    }

    public void handle(Message msg, User from) throws GameServerException {
        if (msg.getMsgDTO() == null) {
            throw new GameServerException(ExceptionType.EVENT_IS_NULL);
        }
        MessageHandler handler = handlers.get(msg.getType());
        if (handler != null) {
            handler.handle(msg, from);
        } else {
            throw new GameServerException(ExceptionType.UNKNOWN_MESSAGE_TYPE, "event type: " + msg.getType());
        }
    }

    private void handleCreateRoomEvent(ClientCreateRoomEvent request, User from) throws GameServerException {
        if (request.getParticipantLimit() <= 0 || request.getDrawTimeLimit() <= 0) {
            throw new GameServerException(ExceptionType.ROOM_CREATION_FAILED, "participants and time limit have to be positive integer");
        }
        roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(), from, sender, exceptionHandler);
    }

    private void handleJoinRoomEvent(ClientJoinRoomEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
    }

    private void handleChangeRoomEvent(ClientChangeRoomSettingEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        if (!room.canChangeSettings(from)) {
            throw new GameServerException(ExceptionType.UNAUTHORIZED);
        }
        room.changeSettings(request.getDrawTimeLimit(), request.getParticipantLimit());
    }

    private void handleGameReadyEvent(ClientReadyEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.setReady(from.getId(), request.getIsReady());
        room.broadCastRoomUpdateEvent();
        room.tryToStartGame();
    }

    private void handleVoteReadyEvent(ClientReadyEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.setVoteReady(from.getId(), request.getIsReady());
        room.tryToStartVote();
    }

    private void handleExitRoomEvent(ClientExitRoomEvent request, User from) throws GameServerException {
        roomManager.deleteUserFrom(from.getId(), from.getRoomId());
    }

    private void handleRoomChatMessage(ClientRoomChatMessage request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.sendChat(from, request.getMessage());
    }

    private void handleSuggestTopicEvent(ClientSuggestTopicEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.getSuggestion(request.getTopic(), from.getId());
    }

    private void handleDrawEvent(ClientDrawEvent request, User from) throws GameServerException {
        Game game = roomManager.getRoom(from.getRoomId()).getGameOnPlay();
        game.drawBy(request.getDrawer(), request.getDrawing(), request.getSubmissionTime());
    }

    private void handleGuessEvent(ClientGuessEvent request, User from) throws GameServerException {
        Game game = roomManager.getRoom(from.getRoomId()).getGameOnPlay();
        game.guess(from.getId(), request.getSubmissionAnswer());
    }

    private void handleVoteEvent(ClientVoteEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.vote(request.getVoteUser(), from.getId());
    }
}