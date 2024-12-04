package domain;

import exception.GameServerException;

public class VoteTimer implements Runnable {

    private final Vote vote;

    public VoteTimer(Vote vote){
        this.vote = vote;
    }

    @Override
    public void run() {
        try {
            vote.finishVote();
        } catch (GameServerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
