package mapper;

import domain.GameRoom;
import dto.info.RoomInfo;

public class RoomMapper {
    public static RoomInfo toRoomInfo(GameRoom room) {
        return new RoomInfo(room.getId(),
                room.getDrawTimeLimit(),
                room.getParticipantLimit(),
                room.getUserList().stream().map(user -> UserMapper.toUserInfo(user, room.isReady(user))).toList());
    }
}
