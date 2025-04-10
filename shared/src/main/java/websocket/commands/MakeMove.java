package websocket.commands;

import chess.ChessPiece;
import chess.ChessPosition;

public class MakeMove extends UserGameCommand {
    private final ChessPosition startingSquare;
    private final ChessPosition endingSquare;
    private final ChessPiece.PieceType promotionPiece;
    public MakeMove(String authToken, Integer gameID, ChessPosition startingSquare, ChessPosition endingSquare, ChessPiece.PieceType promotionPiece) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartingSquare() {
        return startingSquare;
    }

    public ChessPosition getEndingSquare() {
        return endingSquare;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
