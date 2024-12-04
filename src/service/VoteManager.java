package service;

import domain.Room;
import dto.event.server.ServerFinishVoteEvent;
import dto.event.server.ServerRequestVoteEvent;
import exception.GameServerException;
import mapper.VoteMapper;
import message.Message;
import message.MessageType;
import network.Sender;

public class VoteManager implements Runnable {

    private final GameRoomManager roomManager;
    private final int roomID;
    private final Sender sender;

    public VoteManager(GameRoomManager roomManager, int roomID){
        this.roomManager = roomManager;
        this.roomID = roomID;
        this.sender = new Sender(roomManager);
    }

    private void requestVote(Room room) throws GameServerException {
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, new ServerRequestVoteEvent());
        sendMessage(message);
    }

    private void finishVote(Room room) throws GameServerException {
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, new ServerFinishVoteEvent(VoteMapper.toVoteInfo(room)));
        sendMessage(message);
        room.setIsVoteEnd();
    }

    private void sendMessage(Message message) throws GameServerException {
        sender.sendToAll(message, roomID);
    }

    @Override
    public void run() {
        try {
            Room room = roomManager.getRoom(roomID);
            if (room != null){
                while(true){
                    if(room.getIsGameEnd())
                        break;
                }
                requestVote(room);
                wait(30);
                finishVote(room);
            }
        } catch (GameServerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
