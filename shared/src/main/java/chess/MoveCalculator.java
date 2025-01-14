package chess;
import java.util.Collection;
import java.util.ArrayList;

public class MoveCalculator {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece piece;

    public Collection<ChessMove> MoveCalculator(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        this.board = board;
        this.position = position;
        this.piece = piece;
        var pieceType = piece.getPieceType();
        switch (pieceType) {
            case PAWN:
                moves.addAll(PawnMoves(board, position, piece));
                break;
        }
    }
}