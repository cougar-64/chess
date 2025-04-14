package ui;

import chess.*;
import websocket.commands.Connect;

import java.util.ArrayList;
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
    }

    private void initializeBoard(boolean isWhite) {
        String[] files = new String[]{" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        board[0] = new String[10];
        board[9] = new String[10];
        board[0][0] = board[0][9] = board[9][0] = board[9][9] = EscapeSequences.EMPTY;

        for (int i = 0; i < 8; i++) {
            board[0][i + 1] = isWhite ? files[i] : files[7 - i];
            board[9][i + 1] = isWhite ? files[i] : files[7 - i];
        }

        for (int i = 1; i <= 8; i++) {
            String rank = " " + (isWhite ? 9 - i : i) + " ";
            board[i][0] = rank;
            board[i][9] = rank;
        }
    }

    private void updateBoard(boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int boardRow = isWhite ? 8 - i : i + 1;
                int boardCol = isWhite ? j + 1 : 8 - j;

                ChessPiece piece = chessboard.getPiece(new ChessPosition(boardRow, boardCol));
                String pieceSymbol = (piece == null) ? EscapeSequences.EMPTY : getPieceSymbol(piece);
                int displayRow = isWhite ? i + 1 : 8 - i;
                int displayCol = isWhite ? j + 1 : 8 - j;
                board[displayRow][displayCol] = pieceSymbol;
            }
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        boolean isWhitePiece = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        switch (piece.getPieceType()) {
            case KING:
                return isWhitePiece ? " ♔ " : " ♚ ";
            case QUEEN:
                return isWhitePiece ? " ♕ " : " ♛ ";
            case ROOK:
                return isWhitePiece ? " ♖ " : " ♜ ";
            case BISHOP:
                return isWhitePiece ? " ♗ " : " ♝ ";
            case KNIGHT:
                return isWhitePiece ? " ♘ " : " ♞ ";
            case PAWN:
                return isWhitePiece ? " ♙ " : " ♟ ";
            default:
                return EscapeSequences.EMPTY;
        }
    }

    public void printBoardFromWhite(Collection<ChessMove> validMoves) {
        initializeBoard(true);
        updateBoard(true);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            endPositions.add(move.getEndPosition());
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j, true, endPositions);
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack(Collection<ChessMove> validMoves) {
        initializeBoard(false);
        updateBoard(false);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            endPositions.add(move.getEndPosition());
        }
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j--) {
                checkBounds(i, j, false, endPositions);
            }
            System.out.println(reset);
        }
    }

    private void checkBounds(int i, int j, boolean isWhite, ArrayList<ChessPosition> validMoves) {
        if (i == 0 || i == 9 || j == 0 || j == 9) {
            if (i == 0 || i == 9) {
                System.out.print(borderColor + board[i][isWhite ? j : 9 - j]);
            } else {
                if (isWhite) {
                    System.out.print(borderColor + board[i][j]);
                } else {
                    System.out.print(borderColor + board[9 - i][j]);
                }
            }
        } else {
            String piece = board[i][j];
            if (validMoves.contains(new ChessPosition(9-i,j))) {
                System.out.print((highlightColor) + piece + reset);
                return;
            }
            boolean darkOrLight = (i + j) % 2 == 0;
            System.out.print((darkOrLight ? lightColor : darkColor) + piece + reset);
        }
    }
}