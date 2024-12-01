package handler;

import static message.MessageType.*;

import domain.User;
import domain.Room;
import exception.ErrorType;
import mapper.RoomMapper;
import service.GameRoomManager;
import message.Message;
import dto.event.Event;
import dto.event.client.ClientCreateRoomEvent;
import dto.event.client.ClientJoinRoomEvent;
import dto.event.server.ServerCreateRoomEvent;
import dto.event.server.ServerRoomUpdateEvent;
import exception.GameServerException;

public class MessageHandler {
    private final GameRoomManager roomManager;
    private Room room = null;

    public MessageHandler(GameRoomManager roomManager){
        this.roomManager = roomManager;
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
}