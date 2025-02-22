package chess;
import java.util.Collection;
import java.util.ArrayList;

public class MoveCalculator {
    protected ChessBoard board;
    protected ChessPosition position;

    public MoveCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public static ChessMove isNull(int r, int c, ChessPosition position) {
        ChessPosition newPosition = new ChessPosition(r, c);
        return new ChessMove(position, newPosition, null);
    }

    public static ChessMove isNotNull(int r, int c, ChessPosition position, ChessBoard board, ChessGame.TeamColor myColor) {
        ChessPosition newPosition = new ChessPosition(r, c);
        ChessPiece piece = board.getPiece(newPosition);
        if (piece == null)
            return null;
        ChessGame.TeamColor otherColor = piece.getTeamColor();
        if (otherColor != myColor) {
            return new ChessMove(position, newPosition, null);
        } else
            return null;
    }

    public static boolean checkPosition(ChessBoard board, ChessPosition current,
                                        Collection<ChessMove> legalMoves, ChessGame.TeamColor myColor, ChessPosition position) {
        if (board.getPiece(current) == null) {
            ChessMove move = isNull(current.getRow(), current.getColumn(), position);
            legalMoves.add(move);
        }
        else if (board.getPiece(current) != null) {
            // if the piece occupying that square is the other team: add it to the list and break
            ChessMove move = isNotNull(current.getRow(), current.getColumn(), position, board, myColor);
            if (move != null) {
                legalMoves.add(move);
                return true;
            }   else
                return true;
        }
        return false;
    }
}