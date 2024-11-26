package exception;

public class GameServerException extends Exception{
    public GameServerException(String errorMessage) {
        super(errorMessage);
    }

    public GameServerException(String errorMessage, Exception e) {
        super(errorMessage + "\n error: " + e.getMessage());
    }
}