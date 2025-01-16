package chess;
import java.util.Collection;
import java.util.ArrayList;

public class BishopMoves extends MoveCalculator {
    public BishopMoves(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public static Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        ChessPosition oldPosition = new ChessPosition(position.getRow(), position.getColumn());
        ChessGame.TeamColor myColor = (board.getPiece(position)).getTeamColor();

        for (ChessPosition current = position.offset(1, -1); current.getRow() < 8 && current.getColumn() >= 0;
             current = current.offset(1, -1)) {
            if (board.squares[current.getRow()][current.getColumn()] == null) {
                ChessMove move = isNull(current.getRow(), current.getColumn(), position);
                legalMoves.add(move);
            }
            else if (board.squares[current.getRow()][current.getColumn()] != null) {
                // if the piece occupying that square is the other team: add it to the list and break
                ChessMove move = isNotNull(current.getRow(), current.getColumn(), position, board, myColor);
                if (move != null) {
                    legalMoves.add(move);
                    break;
            }   else
                    break;
                }
        }

        for (ChessPosition current = position.offset(-1, -1); current.getRow() >= 0 && current.getColumn() >= 0;
             current = current.offset(-1, -1)) {
            int r = current.getRow();
            int c = current.getColumn();
            if (board.squares[r][c] == null) {
                ChessMove move = isNull(r, c, position);
                legalMoves.add(move);
            }
            else if (board.squares[r][c] != null) {
                ChessMove move = isNotNull(r, c, position, board, myColor);
                if (move != null) {
                    legalMoves.add(move);
                    break;
                }
                else
                    break;
            }
        }

        for (ChessPosition current = position.offset(-1, 1); current.getRow() >= 0 && current.getColumn() < 8;
             current = current.offset(-1, 1)) {
            int r = current.getRow();
            int c = current.getColumn();
            if (board.squares[r][c] == null) {
                ChessMove move = isNull(r, c, position);
                legalMoves.add(move);
            }
            else if (board.squares[r][c] != null) {
                ChessMove move = isNotNull(r, c, position, board, myColor);
                if (move != null) {
                    legalMoves.add(move);
                    break;
                }
                else
                    break;
            }
        }

        for (ChessPosition current = position.offset(1, 1); current.getRow() < 8 && current.getColumn() < 8;
             current = current.offset(1, 1)) {
            int r = current.getRow();
            int c = current.getColumn();
            if (board.squares[r][c] == null) {
                ChessMove move = isNull(r, c, position);
                legalMoves.add(move);
            }
            else if (board.squares[r][c] != null) {
                ChessMove move = isNotNull(r, c, position, board, myColor);
                if (move != null) {
                    legalMoves.add(move);
                    break;
                }
                else
                    break;
            }
        }
        return legalMoves;
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
}


/*
CURRENT ISSUES:
- NOT PRINTING EDGES (8,1) ETC.
- LINE 40 IS TRIGGERING WHEN IT SHOULDN'T. IT'S FINDING BISHOP ON THE NEW SQUARE WHEN IT SHOULDN'T
 */