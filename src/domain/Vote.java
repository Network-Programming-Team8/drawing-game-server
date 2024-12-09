package domain;

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
    private final ConcurrentHashMap<Integer, Integer> voteCount = new ConcurrentHashMap<>();
    private long voteEndTime;
    private static final int VOTE_TIME_LIMIT = 30;
    private final AtomicBoolean isAcceptingVote = new AtomicBoolean(false);

    public Vote(Room room){
        this.room = room;
    }

    public void startVote() throws GameServerException {
        voteEndTime = UnixSeconds.now().plusSeconds(VOTE_TIME_LIMIT).toLong();
        isAcceptingVote.set(true);
        requestVote();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::finishVote, VOTE_TIME_LIMIT, TimeUnit.SECONDS);
    }

    private void requestVote() throws GameServerException {
        Event event = new ServerRequestVoteEvent(voteEndTime);
        Message message = new Message(MessageType.SERVER_REQUEST_VOTE_EVENT, event);
        room.broadcast(message);
    }

    private void finishVote() {
        isAcceptingVote.set(false);
        Event event = new ServerFinishVoteEvent(new VoteInfo(voteCount));
        Message message = new Message(MessageType.SERVER_FINISH_VOTE_EVENT, event);
        try{
            room.broadcast(message);
        } catch (GameServerException e){
            throw new RuntimeException(e);
        }
    }

    public synchronized void vote(int voter, int voteTo) throws GameServerException {
        if (!isAcceptingVote.get()){
            throw new GameServerException(ErrorType.NOT_ACCEPTING_VOTE);
        }
        else{
            updateVote(voter, voteTo);
            broadCastVoteEvent();
        }
    }

    private synchronized void updateVote(int voter, int voteTo) {
        voteStatus.keySet().forEach(userId -> voteCount.putIfAbsent(userId, 0));

        Integer previousVote = voteStatus.put(voter, voteTo);
        if (previousVote != null) {
            voteCount.compute(previousVote, (key, value) -> value == null ? 0 : value - 1);
        }
        voteCount.compute(voteTo, (key, value) -> value == null ? 1 : value + 1);
    }


    private void broadCastVoteEvent() throws GameServerException {
        Event event = new ServerVoteEvent(new VoteInfo(voteCount));
        Message message = new Message(SERVER_VOTE_EVENT, event);
        room.broadcast(message);
    }
}
