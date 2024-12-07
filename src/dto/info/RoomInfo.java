package dto.info;

import java.io.Serial;
import java.util.List;

public class RoomInfo implements Info {

    @Serial
    private static final long serialVersionUID = 4138183250339217845L;
    private final int id;
    private final int drawTimeLimit;
    private final int participantLimit;
    private final List<UserInfo> userInfoList;
    private final int ownerId;

    public RoomInfo(int id, int drawTimeLimit, int participantLimit, List<UserInfo> userInfoList, int ownerId) {
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.userInfoList = userInfoList;
        this.ownerId = ownerId;
    }
}
