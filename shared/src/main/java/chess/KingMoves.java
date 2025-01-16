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
        if (upLeft.getRow() != 0 && upLeft.getRow() > 0 && upLeft.getRow() <=8 && upLeft.getColumn() != 0 && upLeft.getColumn() > 0 && upLeft.getColumn() <=8)
            checkPosition(board, upLeft, legalMoves, myColor, position);

        ChessPosition left = position.offset(0, -1);
        if (left.getRow() != 0 && left.getRow() > 0 && left.getRow() <=8 && left.getColumn() != 0 && left.getColumn() > 0 && left.getColumn() <=8)
            checkPosition(board, left, legalMoves, myColor, position);

        ChessPosition downLeft = position.offset(-1, -1);
        if (downLeft.getRow() != 0 && downLeft.getRow() > 0 && downLeft.getRow() <=8 && downLeft.getColumn() != 0 && downLeft.getColumn() > 0 && downLeft.getColumn() <=8)
            checkPosition(board, downLeft, legalMoves, myColor, position);

        ChessPosition down = position.offset(-1, 0);
        if (down.getRow() != 0 && down.getRow() > 0 && down.getRow() <=8 && down.getColumn() != 0 && down.getColumn() > 0 && down.getColumn() <=8)
            checkPosition(board, down, legalMoves, myColor, position);

        ChessPosition downRight = position.offset(-1, 1);
        if (downRight.getRow() != 0 && downRight.getRow() > 0 && downRight.getRow() <=8 && downRight.getColumn() != 0 && downRight.getColumn() > 0 && downRight.getColumn() <=8)
            checkPosition(board, downRight, legalMoves, myColor, position);

        ChessPosition right = position.offset(0, 1);
        if (right.getRow() != 0 && right.getRow() > 0 && right.getRow() <=8 && right.getColumn() != 0 && right.getColumn() > 0 && right.getColumn() <=8)
            checkPosition(board, right, legalMoves, myColor, position);

        ChessPosition upRight = position.offset(1, 1);
        if (upRight.getRow() != 0 && upRight.getRow() > 0 && upRight.getRow() <=8 && upRight.getColumn() != 0 && upRight.getColumn() > 0 && upRight.getColumn() <=8)
            checkPosition(board, upRight, legalMoves, myColor, position);

        return legalMoves;
    }
}
