package chess;
import java.sql.Array;
import java.util.Collection;
import java.util.ArrayList;

public class KnightMoves extends MoveCalculator {
    public KnightMoves(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    public static Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = (board.getPiece(position)).getTeamColor();

        ChessPosition up2Right1 = position.offset(2, 1);
        if (up2Right1.getRow() != 0 && up2Right1.getRow() > 0 && up2Right1.getRow() <=8 &&
                up2Right1.getColumn() != 0 && up2Right1.getColumn() > 0 && up2Right1.getColumn() <=8) {
            checkPosition(board, up2Right1, legalMoves, myColor, position);
        }

        ChessPosition up1Right2 = position.offset(1, 2);
        if (up1Right2.getRow() != 0 && up1Right2.getRow() > 0 && up1Right2.getRow() <=8 &&
                up1Right2.getColumn() != 0 && up1Right2.getColumn() > 0 && up1Right2.getColumn() <=8) {
            checkPosition(board, up1Right2, legalMoves, myColor, position);
        }

        ChessPosition up2Left1 = position.offset(2, -1);
        if (up2Left1.getRow() != 0 && up2Left1.getRow() > 0 && up2Left1.getRow() <=8 &&
                up2Left1.getColumn() != 0 && up2Left1.getColumn() > 0 && up2Left1.getColumn() <=8) {
            checkPosition(board, up2Left1, legalMoves, myColor, position);
        }

        ChessPosition up1Left2 = position.offset(1, -2);
        if (up1Left2.getRow() != 0 && up1Left2.getRow() > 0 && up1Left2.getRow() <=8 &&
                up1Left2.getColumn() != 0 && up1Left2.getColumn() > 0 && up1Left2.getColumn() <=8) {
            checkPosition(board, up1Left2, legalMoves, myColor, position);
        }

        ChessPosition down1Left2 = position.offset(-1,-2);
        if (down1Left2.getRow() != 0 && down1Left2.getRow() > 0 && down1Left2.getRow() <=8 &&
                down1Left2.getColumn() != 0 && down1Left2.getColumn() > 0 && down1Left2.getColumn() <=8) {
            checkPosition(board, down1Left2, legalMoves, myColor, position);
        }

        ChessPosition down2Left1 = position.offset(-2,-1);
        if (down2Left1.getRow() != 0 && down2Left1.getRow() > 0 && down2Left1.getRow() <=8 &&
                down2Left1.getColumn() != 0 && down2Left1.getColumn() > 0 && down2Left1.getColumn() <=8) {
            checkPosition(board, down2Left1, legalMoves, myColor, position);
        }

        ChessPosition down2Right1 = position.offset(-2,1);
        if (down2Right1.getRow() != 0 && down2Right1.getRow() > 0 && down2Right1.getRow() <=8 &&
                down2Right1.getColumn() != 0 && down2Right1.getColumn() > 0 && down2Right1.getColumn() <=8) {
            checkPosition(board, down2Right1, legalMoves, myColor, position);
        }

        ChessPosition down1Right2 = position.offset(-1,2);
        if (down1Right2.getRow() != 0 && down1Right2.getRow() > 0 && down1Right2.getRow() <=8 &&
                down1Right2.getColumn() != 0 && down1Right2.getColumn() > 0 && down1Right2.getColumn() <=8) {
            checkPosition(board, down1Right2, legalMoves, myColor, position);
        }

        return legalMoves;
    }

}
