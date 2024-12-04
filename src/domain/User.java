package domain;

public class User {

    private final int id;
    private final String nickname;
    private int roomID;

    public User(int id, String nickname){
        this.id = id;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }
    public String getNickname() {
        return nickname;
    }
    public int getRoomID() { return roomID; }
}
