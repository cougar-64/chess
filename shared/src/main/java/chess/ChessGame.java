package chess;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Serializable {
    protected ChessBoard board = new ChessBoard();
    protected TeamColor teamColor = TeamColor.WHITE;

    public ChessGame() {
        board.resetBoard();
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
    public void setTeamTurn(TeamColor team) {
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
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        Collection<ChessMove> finalValidMoves = new ArrayList<>();
        ChessBoard currentBoard = getBoard();
        ChessGame gameCopy = new ChessGame();
        Collection<ChessMove> moves = myMoves(startPosition);
        for (ChessMove move : moves) {
            gameCopy.setBoard(currentBoard.createClone());
            try {
                gameCopy.makeMoveNoCheckValid(move);
            } catch (InvalidMoveException e) {
                System.out.println("Invalid move:" + e.getMessage());
            }
            if (!gameCopy.isInCheck(teamColor)) {
                finalValidMoves.add(move);
            }

        }
        return finalValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean isIn = false;
        ChessBoard currentBoard = getBoard();
        TeamColor myColor = getTeamTurn();
        TeamColor otherColor = myColor.opposite();
        ChessPosition start = move.getStartPosition();
        if (board.getPiece(start) == null) {
            throw new InvalidMoveException();
        }
        ChessPosition end = move.getEndPosition();
        ChessPiece oldPiece = currentBoard.getPiece(move.getStartPosition());
        if (!oldPiece.getTeamColor().equals(myColor)) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        for (ChessMove myMove : moves) {
            if (myMove.equals(move)) {
                isIn = true;
                break;
            }
        }
        if (!isIn) {
            throw new InvalidMoveException();
        }
        if (currentBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException();
        }
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        if (piece != null) {
            if ((piece.getTeamColor()) != myColor) {
                currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
            }
        }
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) && (end.getRow() == 1 || end.getRow() == 8)) {
            currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = new ChessPiece(myColor, move.getPromotionPiece());
        }
        else {
            currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        }
        currentBoard.squares[start.getRow() - 1][start.getColumn() - 1] = null;
        setBoard(currentBoard);
        setTeamTurn(otherColor);
    }


    public void makeMoveNoCheckValid(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        TeamColor myColor = getTeamTurn();
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) && (end.getRow() == 1 || end.getRow() == 8)) {
            board.squares[end.getRow() - 1][end.getColumn() - 1] = new ChessPiece(myColor, move.getPromotionPiece());
        }
        else {
            board.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        }
        board.squares[start.getRow() - 1][start.getColumn() - 1] = null;
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition myKing = new ChessPosition(0, 0);
        ChessBoard currentBoard = getBoard();
        TeamColor opposite = teamColor.opposite();
        Collection<ChessMove> moves = enemyMoves(opposite);
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (currentBoard.getPiece(new ChessPosition(row, col)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING &&
                            currentBoard.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor) {
                        myKing = new ChessPosition(row, col);
                    }
                }
            }
        }
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(myKing)) {
                return true;
            }
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
            Collection<ChessMove> moves = new ArrayList<>();
            sizeMove(moves, teamColor);
            if (moves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            Collection<ChessMove> moves = new ArrayList<>();
            sizeMove(moves, teamColor);
            if (moves.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public Collection<ChessMove> enemyMoves(TeamColor teamColor) {
        ChessBoard currentBoard = getBoard();
        ChessBoard deepCopy = currentBoard.createClone();
        Collection<ChessMove> moves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                findEnemyMoves(deepCopy, row, col, moves, teamColor);
            }
        }
        return moves;
    }


    public void sizeMove(Collection<ChessMove> moves, TeamColor teamColor) {
        ChessBoard currentBoard = getBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (currentBoard.getPiece(new ChessPosition(row, col)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor) {
                        moves.addAll(validMoves(new ChessPosition(row, col)));
                    }
                }
            }
        }
    }

    public void findEnemyMoves(ChessBoard deepCopy, int row, int col, Collection<ChessMove> moves, TeamColor teamColor) {
        ChessPiece piece;
        ChessPiece.PieceType type;
        if (deepCopy.getPiece(new ChessPosition(row, col)) != null) {
            if (deepCopy.getPiece(new ChessPosition(row, col)).getPieceType() != null) {
                if (deepCopy.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor) {
                    piece = deepCopy.getPiece(new ChessPosition(row, col));
                    type = piece.getPieceType();
                    moves.addAll(piece.calculateMove(deepCopy, new ChessPosition(row, col), type));
                }
            }
        }
    }

    public Collection<ChessMove> myMoves(ChessPosition position) {
        ChessBoard currentBoard = getBoard();
        ChessBoard deepCopy = currentBoard.createClone();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece;
        ChessPiece.PieceType type;
        piece = deepCopy.getPiece(position);
        type = piece.getPieceType();
        moves.addAll(piece.calculateMove(deepCopy, position, type));
        return moves;
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