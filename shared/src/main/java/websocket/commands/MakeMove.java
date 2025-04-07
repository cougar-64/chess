package websocket.commands;

public class MakeMove extends UserGameCommand {
    private final String startingSquare;
    private final String endingSquare;
    public MakeMove(String authToken, Integer gameID, String startingSquare, String endingSquare) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
    }

    public String getStartingSquare() {
        return startingSquare;
    }

    public String getEndingSquare() {
        return endingSquare;
    }
}
