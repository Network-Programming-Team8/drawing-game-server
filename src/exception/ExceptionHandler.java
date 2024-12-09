package exception;
import dto.event.server.ServerErrorEvent;
import manager.ConnectionManager;
import message.Message;
import network.Sender;

import java.util.List;

import static message.MessageType.SERVER_ERROR_EVENT;

public class ExceptionHandler {

    private final Sender sender;
    private final ConnectionManager connectionManager;

    public ExceptionHandler(Sender sender, ConnectionManager connectionManager) {
        this.sender = sender;
        this.connectionManager = connectionManager;
    }

    private void sendErrorMessage(Exception e, int id) {
        String errorMessage = e.getMessage();
        Message message = new Message(SERVER_ERROR_EVENT, new ServerErrorEvent(errorMessage));
        sender.send(message, id);
    }

    private void logErrorMessage(Exception e) {
        System.err.println(e.getMessage());
    }

    public void handle(Exception e) {
        System.err.println("error without related connection occurred");
        logErrorMessage(e);
    }

    public void handle(Exception e, int relatedConnectionId) {
        logErrorMessage(e);
        if (e instanceof GameServerException) {
            handleGameServerException((GameServerException) e, relatedConnectionId);
        } else if (e instanceof ConnectionException) {
            handleConnectionException((ConnectionException) e, relatedConnectionId);
        } else {
            handleGeneralException(e, relatedConnectionId);
        }
    }

    public void handle(Exception e, List<Integer> relatedConnectionIds) {
        relatedConnectionIds.forEach(i -> handle(e, i));
    }

    private void handleGameServerException(GameServerException e, int relatedConnectionId) {
        sendErrorMessage(e, relatedConnectionId);
    }

    private void handleConnectionException(ConnectionException e, int relatedConnectionId) {
        connectionManager.closeConnection(relatedConnectionId);
    }

    private void handleGeneralException(Exception e, int relatedConnectionId) {
        sendErrorMessage(e, relatedConnectionId);
    }
}
