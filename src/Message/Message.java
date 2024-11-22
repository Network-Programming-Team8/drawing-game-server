package Message;

import java.io.Serializable;

public class Message implements Serializable {

    private final MessageType msgType;
    private final DTO msgDto;

    public Message(MessageType msgType, DTO msgDto){

        this.msgType = msgType;
        this.msgDto = msgDto;
    }

    public MessageType getType(){
        return msgType;
    }
    public DTO getMsgDTO(){
        return msgDto;
    }
}
