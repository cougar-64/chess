package websocket.commands;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class MakeMove extends UserGameCommand {
    private final ChessMove move;
    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessPosition getStartingSquare() {
        return move.getStartPosition();
    }

    public ChessPosition getEndingSquare() {
        return move.getEndPosition();
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return move.getPromotionPiece();
    }
}
