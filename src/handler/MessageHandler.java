package handler;

import static message.MessageType.*;

import domain.User;
import domain.Room;
import mapper.RoomMapper;
import manager.GameRoomManager;
import mapper.VoteMapper;
import network.Sender;
import message.Message;
import dto.event.Event;
import dto.event.client.*;
import dto.event.server.*;
import exception.ErrorType;
import exception.GameServerException;

public class MessageHandler {
    private final GameRoomManager roomManager;
    private final Sender sender;

    public MessageHandler(GameRoomManager roomManager, Sender sender){
        this.roomManager = roomManager;
        this.sender = sender;
    }

    public void handle(Message msg, User from) throws GameServerException {
        if(msg.getMsgDTO() == null) {
            throw new GameServerException(ErrorType.EVENT_IS_NULL);
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

            case CLIENT_READY_EVENT:
                handleReadyEvent((ClientReadyEvent) (msg.getMsgDTO()), from);
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

            case CLIENT_VOTE_EVENT:
                handleVoteEvent((ClientVoteEvent) (msg.getMsgDTO()), from);
                break;
        }
    }

    private void sendTo(Message message, User to) {
        sender.send(message, to.getId());
    }

    private void broadcastIn(Message message, Room room) throws GameServerException {
        sender.sendToAll(message, room.getUserList().stream().map(User::getId).toList());
    }

    private void handleCreateRoomEvent(ClientCreateRoomEvent request, User from) throws GameServerException {
        if (request.getParticipantLimit() <= 0 || request.getDrawTimeLimit() <= 0) {
            throw new GameServerException(ErrorType.ROOM_CREATION_FAILED, "참가자 수와 제한 시간은 양수여야 합니다.");
        }
        Room room = roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(), from, sender);
        Event event = new ServerCreateRoomEvent(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit());
        Message message = new Message(SERVER_CREATE_ROOM_EVENT, event);
        sendTo(message, from);
    }

    private void handleJoinRoomEvent(ClientJoinRoomEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
        broadCastRoomUpdateEvent(room);
    }

    private void handleChangeRoomEvent(ClientChangeRoomSettingEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        room.changeSettings(request.getDrawTimeLimit(), request.getParticipantLimit());
        broadCastRoomUpdateEvent(room);
    }

    private void handleReadyEvent(ClientReadyEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        room.setReady(from.getId(), request.getIsReady());
        broadCastRoomUpdateEvent(room);
    }

    private void handleExitRoomEvent(ClientExitRoomEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        roomManager.deleteUserFrom(from.getId(), from.getRoomID());
        broadCastRoomUpdateEvent(room);
    }

    private void handleRoomChatMessage(ClientRoomChatMessage request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        room.chatting(from.getNickname(), request.getMessage());
    }

    private void handleSuggestTopicEvent(ClientSuggestTopicEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        room.getSuggestion(request.getTopic(), from.getId());
    }

    private void handleDrawEvent(ClientDrawEvent request, User from) throws GameServerException {

    }

    private void handleGuessEvent(ClientGuessEvent request, User from) throws GameServerException {

    }

    private void handleVoteEvent(ClientVoteEvent request, User from) throws GameServerException {
        Room room = roomManager.getRoom(from.getRoomID());
        Event event;
        Message message;

        if(room.isVoteEnd()){
            event = new ServerErrorEvent("투표 불가: 투표가 이미 종료되었습니다.");
            message = new Message(SERVER_ERROR_EVENT, event);
            sendTo(message, from);
        }
        else{
            int votedUser = request.getVoteUser();
            room.vote(votedUser);
            event = new ServerVoteEvent(VoteMapper.toVoteInfo(room));
            message = new Message(SERVER_VOTE_EVENT, event);
            broadcastIn(message, room);
        }
    }

    private void broadCastRoomUpdateEvent(Room room) throws GameServerException {
        Event event = new ServerRoomUpdateEvent(RoomMapper.toRoomInfo(room));
        Message message = new Message(SERVER_ROOM_UPDATE_EVENT, event);
        broadcastIn(message, room);
    }
}