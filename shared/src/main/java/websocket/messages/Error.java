package websocket.messages;

public class Error extends ServerMessage {
    private String message;
    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
