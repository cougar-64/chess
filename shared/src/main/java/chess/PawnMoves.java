package chess;
import java.util.Collection;
import java.util.ArrayList;

public class PawnMoves extends MoveCalculator {
    public PawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        super(board, position);
    }
    public static Collection<ChessMove> legalMoves() {
        Collection<ChessMove> allLegalMoves = new ArrayList<>();
        // EN PESSANT!!
        // check to see what color the piece is
        // based on the color, check the row the piece is on and if 2 squares forward is available
        // if 2 swuares ahead is available, check for en pessant
        return allLegalMoves;
    }
}
