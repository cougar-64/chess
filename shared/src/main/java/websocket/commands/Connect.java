package websocket.commands;

public class Connect extends UserGameCommand {
    public Connect(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
