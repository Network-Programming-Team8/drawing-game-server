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
import util.UnixSeconds;

import static message.MessageType.SERVER_VOTE_EVENT;

public class Vote {

    private final Room room;
    private final ConcurrentHashMap<Integer, Integer> voteStatus = new ConcurrentHashMap<>();
    private static final int VOTE_TIME_LIMIT = 30;
    private final AtomicBoolean isVoteEnd = new AtomicBoolean(false);

    public Vote(Room room){
        this.room = room;
    }

    public void startVote() throws GameServerException {
        requestVote();
        Thread thread = new Thread(this::voteTimer);
        thread.start();
    }

    private void requestVote() throws GameServerException {
        Event event = new ServerRequestVoteEvent(UnixSeconds.now().plusSeconds(VOTE_TIME_LIMIT).toLong());
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, event);
        room.broadcast(message);
    }

    private void voteTimer() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::finishVote, VOTE_TIME_LIMIT, TimeUnit.SECONDS);
    }

    private void finishVote() {
        isVoteEnd.set(true);
        Event event = new ServerFinishVoteEvent(calculateVoteInfo());
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        try{
            room.broadcast(message);
        } catch (GameServerException e){
            throw new RuntimeException(e);
        }
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

    private synchronized VoteInfo calculateVoteInfo() {
        Map<Integer, Integer> voteCount = new HashMap<>();
        for (Integer userId : room.getUserIdList()) {
            voteCount.put(userId, 0);
        }
        for (Map.Entry<Integer, Integer> entry : voteStatus.entrySet()) {
            Integer votedFor = entry.getValue();
            voteCount.put(votedFor, voteCount.get(votedFor) + 1);
        }
        return new VoteInfo(voteCount);
    }

    public boolean isVoteEnd() { return isVoteEnd.get(); }
}
