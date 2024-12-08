package dto.event.server;

import dto.event.Event;

import java.io.Serial;
import java.time.LocalDateTime;

public class ServerRequestVoteEvent extends Event {

    @Serial
    private static final long serialVersionUID = -9092300978127437216L;
    private final long endTime;

    public ServerRequestVoteEvent(long endTime) {
        this.endTime = endTime;
    }
}
