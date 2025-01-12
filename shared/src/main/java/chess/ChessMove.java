package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return new ChessPosition(0, 0);
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        throw new RuntimeException("Not implemented");
    }
    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        throw new RuntimeException("Not implemented");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessMove move = (ChessMove) obj;
        return this.getStartPosition() == move.getStartPosition() && this.getEndPosition() == move.getEndPosition();
    }
}
