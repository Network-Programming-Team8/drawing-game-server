package dto.info;

import java.io.Serial;
import java.util.concurrent.ConcurrentHashMap;

public class VoteInfo implements Info{

    @Serial
    private static final long serialVersionUID = 3794944618536264364L;
    private final ConcurrentHashMap<Integer, Integer> numberOfVote;

    public VoteInfo(ConcurrentHashMap<Integer, Integer> numberOfVote){
        this.numberOfVote = numberOfVote;
    }
}
