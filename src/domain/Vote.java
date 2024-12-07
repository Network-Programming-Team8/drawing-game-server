package domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        ConcurrentHashMap<Integer, Integer> voteCounter = getVoteCounter();
        Event event = new ServerFinishVoteEvent(new VoteInfo(voteCounter));
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
            broadCastVoteEvents();
        }
    }

    private void broadCastVoteEvents() throws GameServerException {
        ConcurrentHashMap<Integer, Integer> voteCounter = getVoteCounter();
        Event event = new ServerVoteEvent(new VoteInfo(voteCounter));
        Message message = new Message(SERVER_VOTE_EVENT, event);
        room.broadcast(message);
    }

    private ConcurrentHashMap<Integer, Integer> getVoteCounter(){
        ConcurrentHashMap<Integer, Integer> voteCounter = new ConcurrentHashMap<>();
        for(Map.Entry<Integer, Integer> entry: voteStatus.entrySet()){
            voteCounter.compute(entry.getValue(), (k, v) -> v == null ? 1 : v + 1 );
        }
        return voteCounter;
    }

    public boolean isVoteEnd() { return isVoteEnd; }
}
