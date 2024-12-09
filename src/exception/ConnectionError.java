package exception;

public class ConnectionError extends GameServerException {
    public ConnectionError(ErrorType errorType) {
        super(errorType);
    }
}
