package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage {
    private ChessGame game;
    private String playerColor;
    public LoadGame(ChessGame game, String playerColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
