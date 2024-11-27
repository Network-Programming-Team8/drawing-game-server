package handler;

import static message.MessageType.*;

import domain.GameRoom;
import dto.event.Event;
import dto.event.client.ClientCreateRoomEventDTO;
import dto.event.client.ClientJoinRoomEventDTO;
import dto.event.server.ServerCreateRoomEventDTO;
import dto.event.server.ServerJoinRoomEventDTO;
import dto.info.UserInfo;
import exception.GameServerException;
import mapper.RoomMapper;
import message.Message;
import domain.User;
import service.GameRoomManager;

public class MessageHandler {
    private final GameRoomManager roomManager;
    private GameRoom room = null;

    public MessageHandler(GameRoomManager roomManager){
        this.roomManager = roomManager;
    }

    public Message handle(Message msg, User from) throws GameServerException {
        Message response = null;
        switch(msg.getType()){
            case CLIENT_CREATE_ROOM_EVENT:
                response = new Message(SERVER_CREATE_ROOM_EVENT,
                        handleCreateRoomEvent((ClientCreateRoomEventDTO) (msg.getMsgDTO()), from));

            case CLIENT_JOIN_ROOM_EVENT:
                response = new Message(SERVER_JOIN_ROOM_EVENT,
                        handleJoinRoomEvent((ClientJoinRoomEventDTO) (msg.getMsgDTO()), from));
        }
        return response;
    }

    private Event handleCreateRoomEvent(ClientCreateRoomEventDTO request, User from) throws GameServerException {
        if (request.getParticipantLimit() <= 0 || request.getDrawTimeLimit() <= 0) {
            throw new GameServerException("참가자 수와 제한 시간은 양수여야 합니다");
        }
        room = roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(), from);
        return new ServerCreateRoomEventDTO(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit());
    }

    private Event handleJoinRoomEvent(ClientJoinRoomEventDTO request, User from) throws GameServerException {
        room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
        return new ServerJoinRoomEventDTO(RoomMapper.toRoomInfo(room));
    }
}