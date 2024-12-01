package message;

import dto.event.Event;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = -8326171520520566725L;
    private final MessageType msgType;
    private final Event event;

    public Message(MessageType msgType, Event event){
        this.msgType = msgType;
        this.event = event;
    }

    public MessageType getType(){
        return msgType;
    }
    public Event getMsgDTO(){
        return event;
    }
}
