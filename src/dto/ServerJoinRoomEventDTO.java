package dto;

import domain.GameRoom;

public class ServerJoinRoomEventDTO extends DTO{

    private final GameRoom gameRoom;

    public ServerJoinRoomEventDTO(GameRoom gameRoom){

        this.gameRoom = gameRoom;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }
}
