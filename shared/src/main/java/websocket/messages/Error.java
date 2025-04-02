package websocket.messages;

public class Error extends ServerMessage {
    public Error() {
        super(ServerMessageType.ERROR);
    }
}
