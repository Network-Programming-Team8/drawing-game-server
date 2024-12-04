package dto.info;

import java.io.Serial;
import java.util.List;

public class RoomInfo extends Info {

    @Serial
    private static final long serialVersionUID = 4138183250339217845L;
    private final int id;
    private final int drawTimeLimit;
    private final int participantLimit;
    private final List<UserInfo> userInfoList;

    public RoomInfo(int id, int drawTimeLimit, int participantLimit, List<UserInfo> userInfoList) {
        this.id = id;
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
        this.userInfoList = userInfoList;
    }
}
