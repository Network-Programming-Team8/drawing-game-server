package Logic;

import java.util.Map;

import Message.DTO;
import Message.ClientJoinRoomEventDTO;
import Message.ServerJoinRoomEventDTO;

public class GameRoomManager {

    private Map<Integer, GameRoom> gameRoomList;
    private int lastID = 0;

    public DTO createRoom(int drawTimeLimit, int participantLimit){

        GameRoom newRoom = new GameRoom(drawTimeLimit, participantLimit);
        gameRoomList.put(++lastID, newRoom);

        return null;
    }

    public DTO getRoom(DTO dto){

        ClientJoinRoomEventDTO cli_join_room_dto = (ClientJoinRoomEventDTO) dto;
        int roomID = cli_join_room_dto.getRoomID();

        if(gameRoomList.containsKey(roomID))
            return (DTO) new ServerJoinRoomEventDTO(gameRoomList.get(roomID));
        else
            System.out.println("존재하지 않는 방ID 입니다.");

        return null;
    }

    public void deleteRoom(int roomID){

        gameRoomList.remove(roomID);
    }
}
