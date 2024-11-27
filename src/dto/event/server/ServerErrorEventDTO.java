package dto.event.server;

import dto.event.Event;

public class ServerErrorEventDTO extends Event {
    String errorMsg;
    public ServerErrorEventDTO(String errorMsg) {
        errorMsg = this.errorMsg;
    }
}