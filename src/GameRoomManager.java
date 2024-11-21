import java.util.Map;

public class GameRoomManager {

    private Map<Integer, GameRoom> gameRoomList;
    private int lastID = 0;

    GameRoom createRoom(int drawTimeLimit, int participantLimit){

        GameRoom newRoom = new GameRoom(drawTimeLimit, participantLimit);
        gameRoomList.put(lastID++, newRoom);

        return newRoom;
    }

    GameRoom getRoom(int roomID){

        if(gameRoomList.containsKey(roomID))
            return gameRoomList.get(roomID);
        else
            System.out.println("존재하지 않는 방ID 입니다.");

        return null;
    }

    void deleteRoom(int roomID){

        gameRoomList.remove(roomID);
    }
}
