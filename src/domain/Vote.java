package domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dto.event.Event;
import dto.event.server.ServerErrorEvent;
import dto.event.server.ServerFinishVoteEvent;
import dto.event.server.ServerRequestVoteEvent;
import dto.event.server.ServerVoteEvent;
import exception.ErrorType;
import mapper.VoteMapper;
import message.Message;
import message.MessageType;
import exception.GameServerException;
import network.Sender;

import static message.MessageType.SERVER_ERROR_EVENT;
import static message.MessageType.SERVER_VOTE_EVENT;

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
        Event event = new ServerFinishVoteEvent(VoteMapper.toVoteInfo(this));
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

    public void vote(int to, int from) throws GameServerException {
        if (isVoteEnd()) {
            throw new GameServerException(ErrorType.NOT_ACCEPTING_VOTE);
        } else {
            voteCounter.compute(to, (k, v) -> v == null ? 1 : v + 1 );
        }
        broadCastVoteEvent();
    }

    private void broadCastVoteEvent() throws GameServerException {
        Event event = new ServerVoteEvent(VoteMapper.toVoteInfo(this));
        System.out.println();
        Message message = new Message(SERVER_VOTE_EVENT, event);
        room.broadcast(message);
    }

    public boolean isVoteEnd() { return isVoteEnd; }

    public ConcurrentHashMap<Integer, Integer> getVoteState() { return voteCounter; }
}
