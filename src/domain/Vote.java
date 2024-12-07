package domain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import dto.event.Event;
import dto.event.server.ServerFinishVoteEvent;
import dto.event.server.ServerRequestVoteEvent;
import dto.event.server.ServerVoteEvent;
import dto.info.VoteInfo;
import message.Message;
import message.MessageType;
import exception.ErrorType;
import exception.GameServerException;

import static message.MessageType.SERVER_VOTE_EVENT;

public class Vote {

    private final Room room;
    private final ConcurrentHashMap<Integer, Integer> voteStatus = new ConcurrentHashMap<>();
    private final int voteTimeLimit;
    private boolean isVoteEnd;

    public Vote(Room room){
        this.room = room;
        this.voteTimeLimit = 30;
        this.isVoteEnd = false;
    }

    public void startVote() throws GameServerException {
        requestVote();
        Thread thread = new Thread(this::voteTimer);
        thread.start();
    }

    private void requestVote() throws GameServerException {
        Event event = new ServerRequestVoteEvent(voteTimeLimit);
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, event);
        room.broadcast(message);
    }

    private void voteTimer() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::finishVote, voteTimeLimit, TimeUnit.SECONDS);
    }

    private void finishVote() {
        Event event = new ServerFinishVoteEvent(calculateVoteInfo());
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        try{
            room.broadcast(message);
        } catch (GameServerException e){
            throw new RuntimeException(e);
        }
        isVoteEnd = true;
    }

    public synchronized void vote(int to, int from) throws GameServerException {
        if (isVoteEnd()){
            throw new GameServerException(ErrorType.NOT_ACCEPTING_VOTE);
        }
        else{
            voteStatus.put(from, to);
            broadCastVoteEvents();
        }
    }

    private void broadCastVoteEvents() throws GameServerException {
        Event event = new ServerVoteEvent(calculateVoteInfo());
        Message message = new Message(SERVER_VOTE_EVENT, event);
        room.broadcast(message);
    }

    private VoteInfo calculateVoteInfo(){
        Map<Integer, Integer> voteCount = new HashMap<>();
        for (Integer voter : voteStatus.keySet()) {
            Integer votedFor = voteStatus.get(voter);
            voteCount.put(votedFor, voteCount.getOrDefault(votedFor, 0) + 1);
        }
        return new VoteInfo(voteCount);
    }

    public boolean isVoteEnd() { return isVoteEnd; }
}
