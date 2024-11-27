package mapper;

import domain.User;
import dto.info.UserInfo;

public class UserMapper {
    public static UserInfo toUserInfo(User user, boolean isReady) {
        return new UserInfo(user.getID(), user.getNickname(), isReady);
    }
}
