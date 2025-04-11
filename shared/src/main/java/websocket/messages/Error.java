package websocket.messages;

public class Error extends ServerMessage {
    private final String errorMessage;
    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
