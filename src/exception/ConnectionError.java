package exception;

import java.io.Serial;

public class ConnectionError extends GameServerException {
    public ConnectionError(ErrorType errorType) {
        super(errorType);
    }

    public ConnectionError(ErrorType errorType, String additionalMessage) {
        super(errorType, additionalMessage);
    }

    public ConnectionError(ErrorType errorType, Throwable cause, String additionalMessage) {
        super(errorType, cause, additionalMessage);
    }

    public ConnectionError(ErrorType errorType, Throwable cause) {
        super(errorType, cause);
    }
}
