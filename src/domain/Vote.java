package domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final ConcurrentHashMap<Integer, Integer> voteCounter;
    private final int voteTimeLimit;
    private boolean isVoteEnd;

    public Vote(Room room, Sender sender){
        this.sender = sender;
        this.room = room;
        this.voteCounter = new ConcurrentHashMap<>();
        this.voteTimeLimit = 30;
        this.isVoteEnd = false;
    }

    public void startVote() throws GameServerException, InterruptedException {
        requestVote();
        Thread thread = new Thread(this::voteTimer);
        thread.start();
    }

    private void requestVote() throws GameServerException {
        Event event = new ServerRequestVoteEvent(voteTimeLimit);
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, event);
        broadcastIn(message);
    }

    public void voteTimer() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::finishVote, (long)voteTimeLimit, TimeUnit.SECONDS);
    }

    public void finishVote() {
        Event event = new ServerFinishVoteEvent(VoteMapper.toVoteInfo(room));
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        try{
            broadcastIn(message);
        } catch (GameServerException e){
            throw new RuntimeException(e);
        }
        isVoteEnd = true;
    }

    private void broadcastIn(Message message) throws GameServerException {
        sender.sendToAll(message, room.getUserList().stream().map(User::getId).toList());
    }

    public void vote(int id){
        if(voteCounter.containsKey((id))){
            int votedNum = voteCounter.get(id);
            voteCounter.remove(id);
            voteCounter.put(id, votedNum+1);
        }
        else{
            voteCounter.put(id, 1);
        }
    }

    public boolean isVoteEnd() { return isVoteEnd; }

    public ConcurrentHashMap<Integer, Integer> getVoteState() { return voteCounter; }
}
