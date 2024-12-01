package dto.event.client;

import dto.event.Event;

import java.io.Serial;

public class ClientCreateRoomEvent extends Event {

    @Serial
    private static final long serialVersionUID = -8210692791240086745L;
    private final int drawTimeLimit;
    private final int participantLimit;

    public ClientCreateRoomEvent(int drawTimeLimit, int participantLimit){
        this.drawTimeLimit = drawTimeLimit;
        this.participantLimit = participantLimit;
    }

    public int getDrawTimeLimit(){
        return drawTimeLimit;
    }
    public int getParticipantLimit() {
        return participantLimit;
    }
}
