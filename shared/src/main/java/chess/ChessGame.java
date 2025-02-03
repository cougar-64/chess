package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    protected ChessBoard board;
    protected TeamColor teamColor;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team){
        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        if (isInCheck(teamColor)) {
            if (isInCheckmate(teamColor)) {
                possibleMoves.clear();
                return possibleMoves;
            }

        }

        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessBoard currentBoard = getBoard();
        TeamColor myColor = getTeamTurn();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        if (currentBoard.getPiece(move.getStartPosition()) == null)
            throw new InvalidMoveException();
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        if (piece != null) {
            if ((piece.getTeamColor()) != myColor)
                currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        }
        currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        currentBoard.squares[start.getRow() - 1][start.getColumn() - 1] = null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposite = teamColor.opposite();
        ChessBoard currentBoard = getBoard();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece;
        ChessPiece.PieceType type;
        ChessPosition myKing = new ChessPosition(0,0);
        for (int row = 0; row < currentBoard.squares.length; row++) {
            for (int col = 0; col < currentBoard.squares[row].length; col++) {
                if (currentBoard.squares[row][col] != null) {
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING)
                        myKing = new ChessPosition(row, col);
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getTeamColor() == opposite) {
                        piece = currentBoard.getPiece(new ChessPosition(row, col));
                        type = piece.getPieceType();
                        moves = piece.CalculateMove(currentBoard, new ChessPosition(row, col), type);
                    }
                }
            }
        }
        for (ChessMove move : moves) {
            if (move.getEndPosition() == myKing)
                return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            TeamColor opposite = teamColor.opposite();
            ChessBoard currentBoard = getBoard();
            ChessBoard deepCopy = currentBoard.createClone();
            ChessGame gameCopy = new ChessGame();
            gameCopy.setBoard(deepCopy);
            Collection<ChessMove> moves = new ArrayList<>();
            ChessPiece piece;
            ChessPiece.PieceType type;
            ChessPosition myKing = new ChessPosition(0,0);
            for (int row = 0; row < deepCopy.squares.length; row++) {
                for (int col = 0; col < deepCopy.squares[row].length; col++) {
                    if (deepCopy.squares[row][col] != null) {
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING)
                            myKing = new ChessPosition(row, col);
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getTeamColor() == opposite) {
                            piece = deepCopy.getPiece(new ChessPosition(row, col));
                            type = piece.getPieceType();
                            moves = piece.CalculateMove(deepCopy, new ChessPosition(row, col), type);
                        }
                    }
                }
            }
            for (ChessMove move : moves) {
                try {
                    gameCopy.makeMove(move);
                } catch (InvalidMoveException e) {
                    System.out.println("Invalid move:" + e.getMessage());
                }
                if (! isInCheck(teamColor))
                    return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
