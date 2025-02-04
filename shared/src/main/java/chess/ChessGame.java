package chess;

import java.sql.Array;
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
    protected TeamColor teamColor = TeamColor.WHITE;

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
        TeamColor teamColor = getTeamTurn();
        TeamColor myColor = getTeamTurn();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> finalValidMoves = new ArrayList<>();
        if (isInCheck(myColor)) {
            if (isInCheckmate(myColor)) {
                possibleMoves.clear();
                return possibleMoves;
            }
            TeamColor opposite = teamColor.opposite();
            ChessBoard currentBoard = getBoard();
            ChessBoard deepCopy = currentBoard.createClone();
            ChessGame gameCopy = new ChessGame();
            gameCopy.setBoard(deepCopy);
            Collection<ChessMove> moves = new ArrayList<>();
            ChessPiece.PieceType type;
            ChessPosition myKing = new ChessPosition(0,0);
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <=8; col++) {
                    if (deepCopy.getPiece(new ChessPosition(row, col)) != null) {
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING && deepCopy.getPiece(new ChessPosition(row, col)).getTeamColor() == opposite)
                            myKing = new ChessPosition(row, col);
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getTeamColor() == opposite) {
                            piece = deepCopy.getPiece(new ChessPosition(row, col));
                            type = piece.getPieceType();
                            moves.addAll(piece.CalculateMove(deepCopy, new ChessPosition(row, col), type));
                        }
                    }
                }
            }
            while (isInCheck(teamColor)) {
                for (ChessMove move : moves) {
                    try {
                        gameCopy.makeMove(move);
                    } catch (InvalidMoveException e) {
                        System.out.println("Invalid move:" + e.getMessage());
                    }
                    if (!isInCheck(teamColor))
                        finalValidMoves.add(move);
                }
            }
            return finalValidMoves;
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
        boolean IsIn = false;
        ChessBoard currentBoard = getBoard();
        TeamColor myColor = getTeamTurn();
        TeamColor otherColor = myColor.opposite();
        ChessPosition start = move.getStartPosition();
        if (board.getPiece(start) == null)
            throw new InvalidMoveException();
        ChessPosition end = move.getEndPosition();
        ChessPiece oldPiece = currentBoard.getPiece(move.getStartPosition());
        if (! oldPiece.getTeamColor().equals(myColor))
            throw new InvalidMoveException();
//        Collection<ChessMove> moves = oldPiece.CalculateMove(currentBoard, move.getStartPosition(), oldPiece.getPieceType());
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        for (ChessMove myMove : moves) {
            if (myMove.equals(move)) {
                IsIn = true;
                break;
            }
        } if (! IsIn)
            throw new InvalidMoveException();
        if (currentBoard.getPiece(move.getStartPosition()) == null)
            throw new InvalidMoveException();
        ChessPiece piece = currentBoard.getPiece(move.getStartPosition());
        if (piece != null) {
            if ((piece.getTeamColor()) != myColor)
                currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        }
        if (oldPiece.getPieceType().equals(ChessPiece.PieceType.PAWN))
            currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = new ChessPiece(teamColor, move.getPromotionPiece());
        else
            currentBoard.squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        currentBoard.squares[start.getRow() - 1][start.getColumn() - 1] = null;
        setTeamTurn(otherColor);
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
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (currentBoard.getPiece(new ChessPosition(row, col)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor)
                        myKing = new ChessPosition(row, col);
                    if (currentBoard.getPiece(new ChessPosition(row, col)).getTeamColor() == opposite) {
                        piece = currentBoard.getPiece(new ChessPosition(row, col));
                        type = piece.getPieceType();

                        moves.addAll(piece.CalculateMove(currentBoard, new ChessPosition(row, col), type));
                    }
                }
            }
        }
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(myKing))
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
            Collection<ChessMove> finalMoves = new ArrayList<>();
            ChessPiece piece;
            ChessPiece.PieceType type;
            ChessPosition myKing = new ChessPosition(0,0);
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    if (deepCopy.getPiece(new ChessPosition(row, col)) != null) {
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING)
                            myKing = new ChessPosition(row, col);
                        if (deepCopy.getPiece(new ChessPosition(row, col)).getTeamColor() != opposite) {
                            piece = deepCopy.getPiece(new ChessPosition(row, col));
                            type = piece.getPieceType();
                            moves.addAll(piece.CalculateMove(deepCopy, new ChessPosition(row, col), type));
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
                    finalMoves.add(move);
            }
            if (finalMoves.isEmpty())
                return true;
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
        if (board == null)
            return new ChessBoard();
        return board;
    }
}
