package chess;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ArrayList;

public class PawnMoves extends MoveCalculator {
    public PawnMoves(ChessBoard board, ChessPosition position) {
        super(board, position);
    }
    public static Collection<ChessMove> legalMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = (board.getPiece(position).getTeamColor());

        if (myColor == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2) {
                frontLine(board, position, legalMoves, 2, 0, -1);
            }
            if (position.getRow() == 7) {
                ChessPosition offset = position.offset(1,0);
                if (board.getPiece(offset) == null) {
                    straightPromotion(board, position, legalMoves, 1, 0, ChessPiece.PieceType.KNIGHT);
                    straightPromotion(board, position, legalMoves, 1, 0, ChessPiece.PieceType.BISHOP);
                    straightPromotion(board, position, legalMoves, 1, 0, ChessPiece.PieceType.ROOK);
                    straightPromotion(board, position, legalMoves, 1, 0, ChessPiece.PieceType.QUEEN);
                }
                capture(board, position, legalMoves, 1,-1, ChessPiece.PieceType.KNIGHT);
                capture(board, position, legalMoves, 1,-1, ChessPiece.PieceType.BISHOP);
                capture(board, position, legalMoves, 1,-1, ChessPiece.PieceType.ROOK);
                capture(board, position, legalMoves, 1,-1, ChessPiece.PieceType.QUEEN);
                capture(board, position, legalMoves, 1,1, ChessPiece.PieceType.KNIGHT);
                capture(board, position, legalMoves, 1,1, ChessPiece.PieceType.BISHOP);
                capture(board, position, legalMoves, 1,1, ChessPiece.PieceType.ROOK);
                capture(board, position, legalMoves, 1,1, ChessPiece.PieceType.QUEEN);
                return legalMoves;
            }
            move1square(board, position, legalMoves, 1);
            capture(board, position, legalMoves, 1,-1, null);
            capture(board, position, legalMoves, 1,1, null);
        }
        if (myColor == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7) {
                frontLine(board, position, legalMoves, -2, 0, 1);
            }
            if (position.getRow() == 2) {
                ChessPosition offset = position.offset(-1,0);
                if (board.getPiece(offset) == null) {
                    straightPromotion(board, position, legalMoves, -1, 0, ChessPiece.PieceType.KNIGHT);
                    straightPromotion(board, position, legalMoves, -1, 0, ChessPiece.PieceType.BISHOP);
                    straightPromotion(board, position, legalMoves, -1, 0, ChessPiece.PieceType.ROOK);
                    straightPromotion(board, position, legalMoves, -1, 0, ChessPiece.PieceType.QUEEN);
                }
                capture(board, position, legalMoves, -1,-1, ChessPiece.PieceType.KNIGHT);
                capture(board, position, legalMoves, -1,-1, ChessPiece.PieceType.BISHOP);
                capture(board, position, legalMoves, -1,-1, ChessPiece.PieceType.ROOK);
                capture(board, position, legalMoves, -1,-1, ChessPiece.PieceType.QUEEN);
                capture(board, position, legalMoves, -1,1, ChessPiece.PieceType.KNIGHT);
                capture(board, position, legalMoves, -1,1, ChessPiece.PieceType.BISHOP);
                capture(board, position, legalMoves, -1,1, ChessPiece.PieceType.ROOK);
                capture(board, position, legalMoves, -1,1, ChessPiece.PieceType.QUEEN);
                return legalMoves;
            }
            move1square(board, position, legalMoves, -1);
            capture(board, position, legalMoves, -1,-1, null);
            capture(board, position, legalMoves, -1,1, null);
        }

        return legalMoves;
    }

    /*
This function checks to see if the pawn is on the "front line", meaning the row it starts on.
This allows for a potential 2 square move forward and en passant in the future.
 */
    public static ArrayList<ChessMove> frontLine(ChessBoard board, ChessPosition position,
                                                 ArrayList<ChessMove> legalMoves, int r, int c, int check1Square) {
        ChessPosition current = position.offset(r,c);
        if (board.getPiece(current) == null && board.getPiece(position.offset(r+(check1Square), c)) == null) {
            ChessMove move = new ChessMove(position, current, null);
            legalMoves.add(move);
        }
        return legalMoves;
    }

    public static ArrayList<ChessMove> move1square(ChessBoard board, ChessPosition position,
                                                   ArrayList<ChessMove> legalMoves, int offset) {
        ChessPosition current = position.offset(offset, 0);
        ChessMove move;
        if (board.getPiece(current) == null) {
            move = new ChessMove(position, current, null);
            legalMoves.add(move);
        }
        return legalMoves;
    }

    public static ArrayList<ChessMove> capture(ChessBoard board, ChessPosition position,
                                               ArrayList<ChessMove> legalMoves, int r, int c, ChessPiece.PieceType promotion) {
            ChessPosition current = position.offset(r, c);
            if (current.getRow() > 0 && current.getRow() <= 8 && current.getColumn() > 0 && current.getColumn() <= 8) {
                ChessMove move;
                if (board.getPiece(current) != null && board.getPiece(current).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    move = new ChessMove(position, current, promotion);
                    legalMoves.add(move);
                }
                return legalMoves;
            }
            return null;
    }

    public static ArrayList<ChessMove> straightPromotion(ChessBoard board, ChessPosition position,
                                                         ArrayList<ChessMove> legalMoves, int r, int c, ChessPiece.PieceType promotion) {
        ChessPosition current = position.offset(r, c);
        ChessMove move = new ChessMove(position, current, promotion);
        legalMoves.add(move);
        return legalMoves;
    }



    @Override
    public String toString() {
        return "PawnMoves{}";
    }
}
