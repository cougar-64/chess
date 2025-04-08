package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class DrawingBoard {
    String lightColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    String darkColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    String borderColor = EscapeSequences.SET_BG_COLOR_BLACK;
    String reset = EscapeSequences.RESET_BG_COLOR;
    ChessBoard chessboard;
    String[][] board;

    public DrawingBoard(ChessBoard board) {
        this.chessboard = board;
        // Initialize the board as 10x10 to accommodate row and column labels
        this.board = new String[10][10];
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize the top row (labels a-h)
        board[0] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        // Initialize the bottom row (labels a-h again, or row numbers)
        board[9] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        // Initialize row numbers on the left side (1-8)
        for (int i = 1; i <= 8; i++) {
            board[i][0] = " " + (9 - i) + " ";  // Row numbers (1-8) on the left side
        }
    }

    private void updateBoard() {
        // Loop through the 8x8 chessboard (ignoring the row and column labels)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = chessboard.getPiece(new ChessPosition(i + 1, j + 1));
                String pieceSymbol = (piece == null) ? EscapeSequences.EMPTY : getPieceSymbol(piece);
                board[i + 1][j + 1] = pieceSymbol; // Only update the inner 8x8 part
            }
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case KING:
                    return " ♔ ";
                case QUEEN:
                    return " ♕ ";
                case ROOK:
                    return " ♖ ";
                case BISHOP:
                    return " ♗ ";
                case KNIGHT:
                    return " ♘ ";
                case PAWN:
                    return " ♙ ";
                default:
                    return EscapeSequences.EMPTY;
            }
        } else {
            switch (piece.getPieceType()) {
                case KING:
                    return " ♚ ";
                case QUEEN:
                    return " ♛ ";
                case ROOK:
                    return " ♜ ";
                case BISHOP:
                    return " ♝ ";
                case KNIGHT:
                    return " ♞ ";
                case PAWN:
                    return " ♟ ";
                default:
                    return EscapeSequences.EMPTY;
            }
        }
    }

    public void printBoardFromWhite() {
        updateBoard();
        // Print board from the white's perspective (normal order)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j, true);  // Print from white's perspective
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack() {
        updateBoard();
        // Print board from the black's perspective (reversed order)
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j--) {
                checkBounds(i, j, false);  // Print from black's perspective
            }
            System.out.println(reset);
        }
    }

    private void checkBounds(int i, int j, boolean isWhite) {
        if (i == 0 || i == 9 || j == 0 || j == 9) {
            // Border printing: left-most (row numbers) and top/bottom (column labels)
            if (i == 0 || i == 9) {
                // Printing the top or bottom row (columns a-h) or bottom row with numbers
                System.out.print(borderColor + board[i][j]);
            } else if (j == 0) {
                // Printing the leftmost column (row numbers)
                if (isWhite) {
                    System.out.print(borderColor + board[i][j]);  // Left border on white's side (1-8)
                } else {
                    // Print from black's perspective (row numbers should be inverted)
                    System.out.print(borderColor + board[9 - i][j]); // Left border on black's side (1-8, reversed)
                }
            }
        } else {
            boolean darkOrLight = (i + j) % 2 == 0;
            // Adjust for the color scheme
            String piece = board[i][j];
            // Only print the piece symbol, background colors, or row/column numbers
            System.out.print((darkOrLight ? lightColor : darkColor) + piece + reset);
        }
    }
}