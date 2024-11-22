package Logic;

import java.util.Map;

import Message.DTO;
import Message.ClientLoginEventDTO;
import Message.ServerLoginEventDTO;

public class UserManager {

    private Map<Integer, User> userList;
    private int lastID = 0;

    public DTO createUser(DTO dto){

        ClientLoginEventDTO cli_login_dto = (ClientLoginEventDTO) dto;
        User newUser = new User(++lastID, cli_login_dto.getNickName());
        userList.put(lastID, newUser);

        return (DTO) new ServerLoginEventDTO( newUser.getNickname(), newUser.getID());
    }

    public User getUser(int id){    //Only Use When After Login to Receive User Object at ClientController

        return userList.get(id);
    }

    public void join(int id){


    }

    public void leave(int id){


    }
}
