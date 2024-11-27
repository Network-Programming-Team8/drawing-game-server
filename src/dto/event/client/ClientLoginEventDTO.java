package dto.event.client;

import dto.event.Event;

public class ClientLoginEventDTO extends Event {

    private final String nickname;

    public ClientLoginEventDTO(String nickname){
        this.nickname = nickname;
    }

    public String getNickName(){ return nickname; }
}
