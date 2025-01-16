package chess;
import java.util.Collection;
import java.util.ArrayList;

public class KingMoves extends MoveCalculator {
    public KingMoves(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public static Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = (board.getPiece(position)).getTeamColor();

        ChessPosition up = position.offset(1, 0);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, up, legalMoves, myColor, position);

        ChessPosition upLeft = position.offset(1, -1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, upLeft, legalMoves, myColor, position);

        ChessPosition left = position.offset(0, -1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, left, legalMoves, myColor, position);

        ChessPosition downLeft = position.offset(-1, -1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, downLeft, legalMoves, myColor, position);

        ChessPosition down = position.offset(-1, 0);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, down, legalMoves, myColor, position);

        ChessPosition downRight = position.offset(-1, 1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, downRight, legalMoves, myColor, position);

        ChessPosition right = position.offset(0, 1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, right, legalMoves, myColor, position);

        ChessPosition upRight = position.offset(1, 1);
        if (up.getRow() != 0 && up.getRow() > 0 && up.getRow() <=8 && up.getColumn() != 0 && up.getColumn() > 0 && up.getColumn() <=8)
            checkPosition(board, upRight, legalMoves, myColor, position);

        return legalMoves;
    }
}
