package dto.event.server;

import dto.event.Event;

public class ServerLoginEventDTO extends Event {

    private final String nickname;
    private final int id;

    public ServerLoginEventDTO(String nickname, int id){
        this.nickname = nickname;
        this.id = id;
    }
}
