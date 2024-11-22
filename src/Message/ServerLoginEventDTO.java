package Message;

public class ServerLoginEventDTO extends DTO{

    private final String nickname;
    private final int id;

    public ServerLoginEventDTO(String nickname, int id){

        this.type = MessageType.SERVER_LOGIN_EVENT;
        this.nickname = nickname;
        this.id = id;
    }

    String getNickname(){ return nickname; }
    int getId(){ return id; }
}
