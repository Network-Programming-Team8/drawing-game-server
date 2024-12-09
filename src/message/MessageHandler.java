package message;

import static message.MessageType.*;

import domain.Game;
import domain.User;
import domain.Room;
import exception.ExceptionHandler;
import manager.GameRoomManager;
import network.Sender;
import dto.event.Event;
import dto.event.client.*;
import dto.event.server.*;
import exception.ExceptionType;
import exception.GameServerException;

public class MessageHandler {
    private final GameRoomManager roomManager;
    private final Sender sender;
    private final ExceptionHandler exceptionHandler;

    public MessageHandler(GameRoomManager roomManager, Sender sender, ExceptionHandler exceptionHandler){
        this.roomManager = roomManager;
        this.sender = sender;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(Message msg, User from) throws GameServerException, InterruptedException {
        if(msg.getMsgDTO() == null) {
            throw new GameServerException(ExceptionType.EVENT_IS_NULL);
        }
        switch(msg.getType()){
            case CLIENT_CREATE_ROOM_EVENT:
                handleCreateRoomEvent((ClientCreateRoomEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_JOIN_ROOM_EVENT:
                handleJoinRoomEvent((ClientJoinRoomEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_CHANGE_ROOM_SETTING_EVENT:
                handleChangeRoomEvent((ClientChangeRoomSettingEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_GAME_READY_EVENT:
                handleGameReadyEvent((ClientReadyEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_EXIT_ROOM_EVENT:
                handleExitRoomEvent((ClientExitRoomEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_ROOM_CHAT_MESSAGE:
                handleRoomChatMessage((ClientRoomChatMessage) (msg.getMsgDTO()), from);
                break;

            case CLIENT_SUGGEST_TOPIC_EVENT:
                handleSuggestTopicEvent((ClientSuggestTopicEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_DRAW_EVENT:
                handleDrawEvent((ClientDrawEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_GUESS_EVENT:
                handleGuessEvent((ClientGuessEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_VOTE_READY_EVENT:
                handleVoteReadyEvent((ClientReadyEvent) (msg.getMsgDTO()), from);
                break;

            case CLIENT_VOTE_EVENT:
                handleVoteEvent((ClientVoteEvent) (msg.getMsgDTO()), from);
                break;
        }
    }

    private void sendTo(Message message, User to) {
        sender.send(message, to.getId());
    }

    private void broadcastIn(Message message, Room room) {
        sender.sendToAll(message, room.getUserList().stream().map(User::getId).toList());
    }

    private void handleCreateRoomEvent(ClientCreateRoomEvent request, User from) throws GameServerException {
        if (request.getParticipantLimit() <= 0 || request.getDrawTimeLimit() <= 0) {
            throw new GameServerException(ExceptionType.ROOM_CREATION_FAILED, "participants and time limit have to be positive integer");
        }
        Room room = roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(),
                from, sender, exceptionHandler);
        Event event = new ServerCreateRoomEvent(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit(), room.getOwnerId());
        Message message = new Message(SERVER_CREATE_ROOM_EVENT, event);
        sendTo(message, from);
    }

    private void handleJoinRoomEvent(ClientJoinRoomEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
        room.broadCastRoomUpdateEvent();
    }

    private void handleChangeRoomEvent(ClientChangeRoomSettingEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        if(!room.canChangeSettings(from)) {
            throw new GameServerException(ExceptionType.UNAUTHORIZED);
        }
        room.changeSettings(request.getDrawTimeLimit(), request.getParticipantLimit());
        room.broadCastRoomUpdateEvent();
    }

    private void handleGameReadyEvent(ClientReadyEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomId());
        room.setReady(from.getId(), request.getIsReady());
        room.broadCastRoomUpdateEvent();
        room.tryToStartGame();
    }

    private void handleVoteReadyEvent(ClientReadyEvent request, User from) throws GameServerException, InterruptedException {
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