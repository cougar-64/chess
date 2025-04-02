package websocket.commands;

public class MakeMove extends UserGameCommand {
    public MakeMove(String authToken, Integer gameID) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }
}
