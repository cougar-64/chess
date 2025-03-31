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
        this.board = new String[10][10];
        initializeBoard();
    }

    private void initializeBoard() {
        board[0] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        board[9] = new String[]{EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EscapeSequences.EMPTY};
        for (int i = 1; i <= 8; i++) {
            board[i][0] = " " + (9-i) + " ";
            board[9][i] = " " + (9-i) + " ";
        }
    }

    private void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String piece = chessboard.getPiece(new ChessPosition(i, j)).toString();
                board[i+1][j+1] = (piece == null ? EscapeSequences.EMPTY : piece);
            }
        }
    }

    public void printBoardFromWhite() {
        updateBoard();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j);
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack() {
        updateBoard();
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j--) {
                checkBounds(i, j);
            }
            System.out.println(reset);
        }
    }

    private void checkBounds(int i, int j) {
        if (i == 0 || i == 9 || j == 0 || j == 9) {
            System.out.print(borderColor + board[i][j]);
        }
        else {
            boolean darkOrLight = (i + j) % 2 == 0;
            System.out.print((darkOrLight ? lightColor : darkColor) + board[i][j] + reset);
        }
    }
}
