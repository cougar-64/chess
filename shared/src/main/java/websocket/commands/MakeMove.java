package websocket.commands;

import chess.ChessPiece;

public class MakeMove extends UserGameCommand {
    private final String startingSquare;
    private final String endingSquare;
    private final ChessPiece.PieceType promotionPiece;
    public MakeMove(String authToken, Integer gameID, String startingSquare, String endingSquare, ChessPiece.PieceType promotionPiece) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.promotionPiece = promotionPiece;
    }

    public String getStartingSquare() {
        return startingSquare;
    }

    public String getEndingSquare() {
        return endingSquare;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
