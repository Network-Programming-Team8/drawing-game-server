package dto.info;

import java.util.Map;

public class VoteInfo {

    private final Map<Integer, Integer> voteState;

    public VoteInfo(Map<Integer, Integer> voteState){
        this.voteState = voteState;
    }
}
