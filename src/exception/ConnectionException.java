package exception;

public class ConnectionException extends GameServerException {
    public ConnectionException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
