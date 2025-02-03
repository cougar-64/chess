package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType pieceType = getPieceType();
        Collection<ChessMove> possibleMoves = CalculateMove(board, myPosition, pieceType);
        return possibleMoves;
    }

    public Collection<ChessMove> CalculateMove(ChessBoard board, ChessPosition myPosition, ChessPiece.PieceType pieceType) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (pieceType) {
            case BISHOP:
                moves.addAll(BishopMoves.legalMoves(board, myPosition));
                break;
            case ROOK:
                moves.addAll(RookMoves.legalMoves(board, myPosition));
                break;
            case QUEEN:
                moves.addAll(QueenMoves.legalMoves(board, myPosition));
                break;
            case KING:
                moves.addAll(KingMoves.legalMoves(board, myPosition));
                break;
            case KNIGHT:
                moves.addAll(KnightMoves.legalMoves(board, myPosition));
                break;
            case PAWN:
                moves.addAll(PawnMoves.legalMoves(board, myPosition));
                break;
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
