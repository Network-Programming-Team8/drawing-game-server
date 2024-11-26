package exception;

public class GameServerException extends Exception{
    public GameServerException(String errorMessage) {
        super(errorMessage);
    }
}