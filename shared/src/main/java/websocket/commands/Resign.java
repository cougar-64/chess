package websocket.commands;

public class Resign extends UserGameCommand {
    public Resign(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
