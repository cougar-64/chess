package ui;

import chess.*;

import java.util.Collection;

public class DrawingBoard {
    String lightColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    String darkColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    String highlightColor = EscapeSequences.SET_BG_COLOR_GREEN;
    String borderColor = EscapeSequences.SET_BG_COLOR_BLACK;
    String reset = EscapeSequences.RESET_BG_COLOR;
    ChessBoard chessboard;
    String[][] board;

    public DrawingBoard(ChessBoard board) {
        this.chessboard = board;
        this.board = new String[10][10];
        initializeBoard();
    }

    private void initializeBoard() {
        board[0] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        board[9] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        for (int i = 1; i <= 8; i++) {
            board[i][0] = " " + (9 - i) + " ";
            board[i][9] = " " + (9 - i) + " ";
        }
    }

    private void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = chessboard.getPiece(new ChessPosition(i + 1, j + 1));
                String pieceSymbol = (piece == null) ? EscapeSequences.EMPTY : getPieceSymbol(piece);
                board[i + 1][j + 1] = pieceSymbol;
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
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j, true);
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack() {
        updateBoard();
        String[] reversed = new String[]{EscapeSequences.EMPTY, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EscapeSequences.EMPTY};
        board[0] = reversed;
        board[9] = reversed;
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j--) {
                checkBounds(i, j, false);
            }
            System.out.println(reset);
        }
    }

    private void checkBounds(int i, int j, boolean isWhite) {
        if (i == 0 || i == 9 || j == 0 || j == 9) {
            if (i == 0 || i == 9) {
                System.out.print(borderColor + board[i][j]);
            } else {
                if (isWhite) {
                    System.out.print(borderColor + board[i][j]);
                } else {
                    System.out.print(borderColor + board[9 - i][j]);
                }
            }
        } else {
            boolean darkOrLight = (i + j) % 2 == 0;
            String piece = board[i][j];
            System.out.print((darkOrLight ? lightColor : darkColor) + piece + reset);
        }
    }

    public void highlight(Collection<ChessMove> validMoves, String playerColor) {
        updateBoard();


        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                int finalI = i;
                int finalJ = j;
                boolean isValidMove = validMoves.stream().anyMatch(move ->
                        move.getEndPosition().equals(new ChessPosition(finalI, finalJ)));

                if (isValidMove) {
                    highlightSquare(i, j, true);
                } else {
                    checkBounds(i, j, playerColor.equals("WHITE"));
                }
            }
            System.out.println(reset);
        }
    }
    private void highlightSquare(int i, int j, boolean isValidMove) {
        if (isValidMove) {
            System.out.print(highlightColor + board[i][j] + reset);
        }
    }
}