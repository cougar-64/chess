package chess;
import java.util.Collection;
import java.util.ArrayList;

public class MoveCalculator {
    protected ChessBoard board;
    protected static ChessPosition position;

    public MoveCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }
}