package exception;

public class ConnectionException extends GameServerException {
    public ConnectionException(ErrorType errorType) {
        super(errorType);
    }
}
