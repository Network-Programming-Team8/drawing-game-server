package exception;

import java.io.Serial;

public class ConnectionError extends GameServerException {
    public ConnectionError(ErrorType errorType) {
        super(errorType);
    }
}
