package domain;

import java.util.Map;
import java.util.HashMap;

import dto.event.Event;
import dto.event.server.ServerFinishVoteEvent;
import dto.event.server.ServerRequestVoteEvent;
import mapper.VoteMapper;
import message.Message;
import message.MessageType;
import exception.GameServerException;
import network.Sender;

public class Vote {

    private final Sender sender;
    private final Room room;
    private final Map<Integer, Integer> voteCounter;
    private final int voteTimeLimit;
    private boolean isVoteEnd;

    public Vote(Room room, Sender sender){
        this.sender = sender;
        this.room = room;
        this.voteCounter = new HashMap<>();
        this.voteTimeLimit = 30;
        this.isVoteEnd = false;
        room.vote = this;
    }

    public void startVote() throws GameServerException, InterruptedException {
        requestVote();
        Thread voteTimer = new Thread(new VoteTimer(this));
        voteTimer.start();
    }

    private void requestVote() throws GameServerException {
        Event event = new ServerRequestVoteEvent(voteTimeLimit);
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, event);
        broadcastIn(message);
    }

    public void finishVote() throws GameServerException, InterruptedException {
        wait(voteTimeLimit * 1000L);

        Event event = new ServerFinishVoteEvent(VoteMapper.toVoteInfo(room));
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        broadcastIn(message);
        isVoteEnd = true;
    }

    private void broadcastIn(Message message) throws GameServerException {
        sender.sendToAll(message, room.getUserList().stream().map(User::getId).toList());
    }

    public void vote(int id){
        if(voteCounter.containsKey((id))){
            voteCounter.put(id, voteCounter.get(id)+1);
        }
        else{
            voteCounter.put(id, 1);
        }
    }

    public boolean isVoteEnd() { return isVoteEnd; }

    public Map<Integer, Integer> getVoteState() { return voteCounter; }
}
