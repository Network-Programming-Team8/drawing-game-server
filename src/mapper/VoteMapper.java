package mapper;

import domain.Vote;
import dto.info.VoteInfo;

public class VoteMapper {
    public static VoteInfo toVoteInfo(Vote vote){
        return new VoteInfo(vote.getVoteState());
    }
}
