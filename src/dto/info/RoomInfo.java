package dto.info;

import dto.event.Event;

import java.io.Serial;
import java.util.List;

public class RoomInfo extends Event {

    @Serial
    private static final long serialVersionUID = 1L;
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
