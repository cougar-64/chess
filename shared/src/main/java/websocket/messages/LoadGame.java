package websocket.messages;

public class LoadGame extends ServerMessage {
    public LoadGame() {
        super(ServerMessageType.LOAD_GAME);
    }
}
