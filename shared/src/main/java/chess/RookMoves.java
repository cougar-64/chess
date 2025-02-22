package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMoves extends MoveCalculator{
    public RookMoves(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public static Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = (board.getPiece(position)).getTeamColor();

        for (ChessPosition current = position.offset(0,1); current.getColumn() <= 8; current = current.offset(0,1)) {
            if (checkPosition(board, current, legalMoves, myColor, position)) {
                break;
            }
        }

        for (ChessPosition current = position.offset(0, -1); current.getColumn() > 0; current = current.offset(0, -1)) {
            if (checkPosition(board, current, legalMoves, myColor, position)) {
                break;
            }
        }

        for (ChessPosition current = position.offset(1,0); current.getRow() <= 8; current = current.offset(1, 0)) {
            if (checkPosition(board, current, legalMoves, myColor, position)) {
                break;
            }
        }

        for (ChessPosition current = position.offset(-1, 0); current.getRow() > 0; current = current.offset(-1, 0)) {
            if (checkPosition(board, current, legalMoves, myColor, position)) {
                break;
            }
        }
        return legalMoves;
    }
}
