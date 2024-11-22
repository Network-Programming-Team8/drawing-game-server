package Message;

import java.io.Serializable;

public class Message implements Serializable {

    private MessageType msgType;
    private DTO dto;

    public Message(MessageType msgType, DTO dto){

        this.msgType = msgType;
        this.dto = dto;
    }

    public MessageType getType(){ return msgType; }
    public DTO getDto(){ return dto; }
}
