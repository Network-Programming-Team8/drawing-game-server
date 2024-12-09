package exception;

public class GameServerException extends Exception {
    private ErrorType errorType;

    public GameServerException(ErrorType errorType) {
        super(errorType.toString());
        this.errorType = errorType;
    }

    public GameServerException(ErrorType errorType, String additionalMessage) {
        super(String.format("%s: %s", errorType.toString(), additionalMessage));
    }

    public GameServerException(ErrorType errorType, Throwable cause, String additionalMessage) {
        super(String.format("%s: %s", errorType.toString(), additionalMessage), cause);
    }

    public GameServerException(ErrorType errorType, Throwable cause) {
        super(errorType.toString(), cause);
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
