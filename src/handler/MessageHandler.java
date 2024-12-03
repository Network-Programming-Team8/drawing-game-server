package handler;

import static message.MessageType.*;

import domain.User;
import domain.Room;
import mapper.RoomMapper;
import mapper.VoteMapper;
import service.GameRoomManager;
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
    private Room room = null;

    public MessageHandler(GameRoomManager roomManager, Sender sender){
        this.roomManager = roomManager;
        this.sender = sender;
    }

    public Message handle(Message msg, User from) throws GameServerException {
        Message response = null;
        switch(msg.getType()){
            case CLIENT_CREATE_ROOM_EVENT:
                response = new Message(SERVER_CREATE_ROOM_EVENT,
                        handleCreateRoomEvent((ClientCreateRoomEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_JOIN_ROOM_EVENT:
                response = new Message(SERVER_ROOM_UPDATE_EVENT,
                        handleJoinRoomEvent((ClientJoinRoomEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_CHANGE_ROOM_SETTING_EVENT:
                response = new Message(SERVER_ROOM_UPDATE_EVENT,
                        handleChangeRoomEvent((ClientChangeRoomSettingEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_READY_EVENT:
                response = new Message(SERVER_ROOM_UPDATE_EVENT,
                        handleReadyEvent((ClientReadyEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_EXIT_ROOM_EVENT:
                response = new Message(SERVER_ROOM_UPDATE_EVENT,
                        handleExitRoomEvent((ClientExitRoomEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_ROOM_CHAT_MESSAGE:
                response = new Message(SERVER_ROOM_CHAT_MESSAGE,
                        handleRoomChatMessage((ClientRoomChatMessage) (msg.getMsgDTO()), from));
                break;

            case CLIENT_SUGGEST_TOPIC_EVENT:
                response = new Message(SERVER_START_GAME_EVENT,
                        handleSuggestTopicEvent((ClientSuggestTopicEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_DRAW_EVENT:
                response = new Message(SERVER_DRAW_EVENT,
                        handleDrawEvent((ClientDrawEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_GUESS_EVENT:
                response = new Message(SERVER_FINISH_GAME_EVENT,
                        handleGuessEvent((ClientGuessEvent) (msg.getMsgDTO()), from));
                break;

            case CLIENT_VOTE_EVENT:
                response = new Message(SERVER_VOTE_EVENT,
                        handleVoteEvent((ClientVoteEvent) (msg.getMsgDTO()), from));
                break;
        }
        return response;
    }

    private Event handleCreateRoomEvent(ClientCreateRoomEvent request, User from) throws GameServerException {
        if (request.getParticipantLimit() <= 0 || request.getDrawTimeLimit() <= 0) {
            throw new GameServerException(ErrorType.ROOM_CREATION_FAILED, "참가자 수와 제한 시간은 양수여야 합니다.");
        }
        room = roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(), from);
        return new ServerCreateRoomEvent(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit());
    }

    private Event handleJoinRoomEvent(ClientJoinRoomEvent request, User from) throws GameServerException {
        room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
        return new ServerRoomUpdateEvent(RoomMapper.toRoomInfo(room));
    }

    private Event handleChangeRoomEvent(ClientChangeRoomSettingEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleReadyEvent(ClientReadyEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleExitRoomEvent(ClientExitRoomEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleRoomChatMessage(ClientRoomChatMessage request, User from) throws GameServerException {

        return null;
    }

    private Event handleSuggestTopicEvent(ClientSuggestTopicEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleDrawEvent(ClientDrawEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleGuessEvent(ClientGuessEvent request, User from) throws GameServerException {

        return null;
    }

    private Event handleVoteEvent(ClientVoteEvent request, User from) throws GameServerException {
        int votedUser = request.getVoteUser();
        room.vote(votedUser);
        return new ServerVoteEvent(VoteMapper.toVoteInfo(room));
    }
}