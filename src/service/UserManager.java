package service;

import java.util.HashMap;
import java.util.Map;

import domain.User;

public class UserManager {

    private final Map<Integer, User> userList = new HashMap<>();
    private int lastID = 0;

    public User createUser(String nickName){
        User newUser = new User(++lastID, nickName);
        userList.put(lastID, newUser);
        return newUser;
    }

    public User getUser (int id) {    //Only Use When After Login to Receive User Object at ClientController
        return userList.get(id);
    }

    public void deleteUser (int id) {
        userList.remove(id);
    }
}