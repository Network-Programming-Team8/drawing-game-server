package exception;

public class GameServerException extends Exception {
    private ExceptionType exceptionType;

    public GameServerException(ExceptionType exceptionType) {
        super(exceptionType.toString());
        this.exceptionType = exceptionType;
    }

    public GameServerException(ExceptionType exceptionType, String additionalMessage) {
        super(String.format("%s: %s", exceptionType.toString(), additionalMessage));
    }

    public GameServerException(ExceptionType exceptionType, Throwable cause, String additionalMessage) {
        super(String.format("%s: %s", exceptionType.toString(), additionalMessage), cause);
    }

    public GameServerException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.toString(), cause);
    }

    public ExceptionType getErrorType() {
        return exceptionType;
    }
}
