package handler;

import static message.MessageType.*;

import domain.GameRoom;
import dto.*;
import exception.GameServerException;
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

    private DTO handleCreateRoomEvent(ClientCreateRoomEventDTO request, User from) throws GameServerException {
        room = roomManager.createRoom(request.getDrawTimeLimit(), request.getParticipantLimit(), from);
        return new ServerCreateRoomEventDTO(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit());
    }

    private DTO handleJoinRoomEvent(ClientJoinRoomEventDTO request, User from) throws GameServerException {
        room = roomManager.getRoom(request.getRoomID());
        room.addUser(from);
        return new ServerJoinRoomEventDTO(room.getId(), room.getDrawTimeLimit(), room.getParticipantLimit(), room.getUserList());
    }
}