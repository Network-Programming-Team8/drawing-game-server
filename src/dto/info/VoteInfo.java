package dto.info;

import java.io.Serial;
import java.util.Map;
import java.util.stream.Collectors;

public class VoteInfo implements Info{

    @Serial
    private static final long serialVersionUID = 3794944618536264364L;
    private final Map<Integer, Integer> numberOfVote;

    public VoteInfo(Map<Integer, Integer> numberOfVote){
        this.numberOfVote = numberOfVote;
    }

    @Override
    public String toString() {
        return numberOfVote.entrySet().stream().map(
                (entry) -> String.format("user %d got %d votes", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("%n"));
    }
}
