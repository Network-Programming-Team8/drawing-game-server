package exception;

public enum ExceptionType {

    // **General Errors**
    UNKNOWN_ERROR("E000", "An unknown error occurred."),
    INVALID_INPUT("E001", "Invalid input."),
    SYSTEM_FAILURE("E002", "An internal system error occurred."),
    EVENT_IS_NULL("E003", "The event object in the message is null."),

    // **Connection Errors**
    CLIENT_DISCONNECTED("E100", "The client has disconnected."),
    MESSAGE_SEND_ERROR("E101", "An error occurred while sending the message."),
    MESSAGE_RECEIVE_ERROR("E102", "An error occurred while receiving the message."),
    CONNECTION_NOT_FOUND("E103", "The connection was not found."),
    FAILED_TO_CONNECT("E104", "Failed to establish the connection."),

    // **Room Errors**
    ROOM_NOT_FOUND("E200", "The room with the given ID does not exist."),
    ROOM_CREATION_FAILED("E201", "Failed to create the room."),
    ROOM_JOIN_FAILED("E202", "Failed to join the room."),
    OWNER_SELECT_FAILED("E203", "The room has only one member and cannot assign a new owner."),

    // **User Errors**
    USER_CREATION_FAILED("E300", "Failed to create the user."),
    USER_NOT_FOUND("E301", "The user was not found."),
    USER_ALREADY_IN_ROOM("E302", "The user is already in the room."),
    USER_IS_NOT_IN_ROOM("E303", "The user is not in the game room."),

    // **Game Errors**
    GAME_START_FAILED("E400", "Failed to start the game."),
    GAME_ACTION_INVALID("E401", "Invalid game action."),
    GAME_ROOM_STATE_INVALID("E402", "The game room state is invalid."),
    NO_GAME_RUNNING("E403", "No game is currently running."),
    DRAWER_OUT_OF_ORDER("E404", "It is not the user's turn to draw."),
    GUESS_FROM_NONE_GUESSER("E405", "The user is not a guesser in this game."),
    SUBMISSION_OUT_OF_TIME("E406", "The submission is out of time."),

    // **Vote Errors**
    NOT_ACCEPTING_VOTE("E501", "It is not the voting time."),

    // **Permission Errors**
    UNAUTHORIZED("E600", "An unauthorized user attempted to change the settings.");

    private final String errorCode;
    private final String errorMessage;

    ExceptionType(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, errorMessage);
    }
}
