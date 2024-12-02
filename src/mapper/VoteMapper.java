package mapper;

import domain.Room;
import dto.info.VoteInfo;

public class VoteMapper {
    public static VoteInfo toVoteInfo(Room room){
        return new VoteInfo(room.getVoteState());
    }
}
