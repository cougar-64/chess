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
        initializeBoard(true);
        updateBoard(true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j, true);
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack() {
        initializeBoard(false);
        updateBoard(false);
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
                System.out.print(borderColor + board[i][isWhite ? j : 9 - j]);
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
        boolean isWhite = playerColor.equals("WHITE");
        initializeBoard(isWhite);
        updateBoard(isWhite);

        ChessPosition startPos = validMoves.iterator().next().getStartPosition();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    checkBounds(i, j, isWhite);
                } else {
                    ChessPosition currentPos = new ChessPosition(
                            isWhite ? 8 - i + 1 : i,
                            isWhite ? j : 9 - j
                    );
                    boolean isStart = currentPos.equals(startPos);
                    boolean isValidMove = validMoves.stream()
                            .anyMatch(move -> move.getEndPosition().equals(currentPos));

                    if (isStart) {
                        System.out.print(highlightColor + board[i][j] + reset);
                    } else if (isValidMove) {
                        System.out.print(highlightColor + board[i][j] + reset);
                    } else {
                        boolean darkOrLight = (i + j) % 2 == 0;
                        System.out.print((darkOrLight ? lightColor : darkColor) + board[i][j] + reset);
                    }
                }
            }
            System.out.println(reset);
        }
    }
}