package exception;

public enum ErrorType {

    // **General Errors**
    UNKNOWN_ERROR("E000", "알 수 없는 오류가 발생했습니다."),
    INVALID_INPUT("E001", "잘못된 입력입니다."),
    SYSTEM_FAILURE("E002", "시스템 내부 오류가 발생했습니다."),
    EVENT_IS_NULL("E003", "메세지 내 이벤트 객체가 null입니다."),

    // **Connection Errors**
    CLIENT_DISCONNECTED("E100", "클라이언트가 연결을 종료했습니다."),
    MESSAGE_SEND_ERROR("E101", "메세지 송신 중 오류가 발생했습니다."),
    MESSAGE_RECEIVE_ERROR("E102", "메시지 수신 중 오류가 발생했습니다."),
    CONNECTION_NOT_FOUND("E103", "연결을 찾을 수 없습니다."),
    FAILED_TO_CONNECT("E104", "연결에 실패했습니다."),

    // **Room Errors**
    ROOM_NOT_FOUND("E200", "해당 ID의 방이 존재하지 않습니다."),
    ROOM_CREATION_FAILED("E201", "방 생성에 실패했습니다."),
    ROOM_JOIN_FAILED("E202", "방에 참여에 실패했습니다."),

    // **User Errors**
    USER_CREATION_FAILED("E300", "유저 생성에 실패했습니다."),
    USER_NOT_FOUND("E301", "해당 유저를 찾을 수 없습니다."),
    USER_ALREADY_IN_ROOM("E302", "유저가 이미 방에 참가해 있습니다."),
    USER_IS_NOT_IN_ROOM("E303", "유저가 게임 룸 안에 없습니다."),

    // **Game Errors**
    GAME_START_FAILED("E400", "게임을 시작하는 데 실패했습니다."),
    GAME_ACTION_INVALID("E401", "잘못된 게임 액션입니다."),
    GAME_ROOM_STATE_INVALID("E402", "게임 방 상태가 유효하지 않습니다."),
    NO_GAME_RUNNING("E403", "실행 중인 게임이 없습니다."),
    DRAWER_OUT_OF_ORDER("E404", "현재 해당 유저의 그리기 순서가 아닙니다."),
    GUESS_FROM_NONE_GUESSER("E405", "해당 유저는 이번 게임의 맞추는 사람이 아닙니다."),
    SUBMISSION_OUT_OF_TIME("E406", "제한 시간이 지났습니다."),

    // **Vote Errors**
    NOT_ACCEPTING_VOTE("E501", "투표 시간이 아닙니다."),

    // **Permission Errors**
    UNAUTHORIZED("E600", "권한이 없는 유저가 설정을 변경하려고 시도했습니다.");

    private final String errorCode;
    private final String errorMessage;

    ErrorType(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, errorMessage);
    }
}