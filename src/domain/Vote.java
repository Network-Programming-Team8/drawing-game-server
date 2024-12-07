package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dto.event.Event;
import dto.event.server.ServerFinishVoteEvent;
import dto.event.server.ServerRequestVoteEvent;
import dto.event.server.ServerVoteEvent;
import dto.info.VoteInfo;
import exception.ErrorType;
import message.Message;
import message.MessageType;
import exception.GameServerException;
import network.Sender;

import static message.MessageType.SERVER_VOTE_EVENT;

public class Vote {

    private final Room room;
    private final ConcurrentHashMap<Integer, Integer> voteStatus = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> voteCounter = new ConcurrentHashMap<>();
    private final int voteTimeLimit;
    private boolean isVoteEnd;

    public Vote(Room room, Sender sender){
        this.room = room;
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
        room.broadcast(message);
    }

    public void voteTimer() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::finishVote, (long)voteTimeLimit, TimeUnit.SECONDS);
    }

    public void finishVote() {
        VoteInfo voteInfo = new VoteInfo(new HashMap<>(voteCounter));
        Event event = new ServerFinishVoteEvent(voteInfo);
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        try{
            room.broadcast(message);
        } catch (GameServerException e){
            throw new RuntimeException(e);
        }
        isVoteEnd = true;
    }

    public void vote(int to, int from) throws GameServerException {
        if (isVoteEnd()){
            throw new GameServerException(ErrorType.NOT_ACCEPTING_VOTE);
        }
        else{
            voteStatus.put(from, to);
            broadCastVote();
        }
    }

    public void broadCastVote() throws GameServerException {
        voteCounter = new ConcurrentHashMap<>();
        for(Map.Entry<Integer, Integer> entry: voteStatus.entrySet()){
            voteCounter.compute(entry.getValue(), (k, v) -> v == null ? 1 : v + 1 );
        }
        broadCastVoteEvent();
    }

    private void broadCastVoteEvent() throws GameServerException {
        Event event = new ServerVoteEvent(new VoteInfo(voteCounter));
        Message message = new Message(SERVER_VOTE_EVENT, event);
        room.broadcast(message);
    }

    public boolean isVoteEnd() { return isVoteEnd; }
}
