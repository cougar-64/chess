package ui;

public class DrawingBoard {
    String lightColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY;
    String darkColor = EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY;
    String borderColor = EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY;
    String reset = EscapeSequences.RESET_BG_COLOR;

    String[][] board = {
            {EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h", EscapeSequences.EMPTY},
            {"8", EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN,
                EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK, "8"},
            {"7", EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                    EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, "7"},
            {"6", EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, "7"},
            {"5", EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, "5"},
            {"4", EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, "4"},
            {"3", EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                    EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, "3"},
            {"2", EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                    EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, "2"},
            {"1", EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN,
                    EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK, "1"},
            {EscapeSequences.EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h", EscapeSequences.EMPTY}
    };

    public void printBoardFromWhite() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                checkBounds(i, j);
            }
            System.out.println(reset);
        }
    }

    public void printBoardFromBlack() {
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j--) {
                checkBounds(i, j);
            }
            System.out.println(reset);
        }
    }

    public static void main(String[] args) {
        DrawingBoard draw = new DrawingBoard();
        draw.printBoardFromWhite();
        System.out.println("\n");
        draw.printBoardFromBlack();
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
