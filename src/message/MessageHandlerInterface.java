package message;

import domain.User;
import exception.GameServerException;

@FunctionalInterface
public interface MessageHandlerInterface {
    void handle(Message message, User from) throws GameServerException;
}