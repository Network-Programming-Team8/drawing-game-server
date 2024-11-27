package dto.event.client;

import dto.event.Event;

public class ClientCreateRoomEventDTO extends Event {
    int drawTimeLimit;
    int participantLimit;

    public ClientCreateRoomEventDTO(int drawTimeLimit, int participantLimit){
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
