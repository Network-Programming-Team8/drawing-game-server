package mapper;

import domain.Room;
import dto.info.RoomInfo;

public class RoomMapper {
    public static RoomInfo toRoomInfo(Room room) {
        return new RoomInfo(room.getId(),
                room.getDrawTimeLimit(),
                room.getParticipantLimit(),
                room.getUserList().stream().map(user -> UserMapper.toUserInfo(user, room.isReady(user.getId()))).toList());
    }
}