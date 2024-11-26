package message;

public enum MessageType {
    SERVER_ERROR_EVENT,

    CLIENT_LOGIN_EVENT,
    SERVER_LOGIN_EVENT,

    CLIENT_CREATE_ROOM_EVENT,
    SERVER_CREATE_ROOM_EVENT,

    CLIENT_JOIN_ROOM_EVENT,
    SERVER_JOIN_ROOM_EVENT,

    CLIENT_ROOM_SETTING_EVENT,
    SERVER_ROOM_SETTING_EVENT,

    CLIENT_ROOM_CHAT_MESSAGE,
    SERVER_ROOM_CHAT_MESSAGE,

    CLIENT_EXIT_ROOM_EVENT,

    SERVER_UPDATE_ROOM_EVENT
}