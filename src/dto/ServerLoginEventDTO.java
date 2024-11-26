package dto;

public class ServerLoginEventDTO extends DTO{

    private final String nickname;
    private final int id;

    public ServerLoginEventDTO(String nickname, int id){

        this.nickname = nickname;
        this.id = id;
    }

    public String getNickname(){ return nickname; }
    public int getId(){ return id; }
}
