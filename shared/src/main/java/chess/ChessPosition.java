package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public ChessPosition offset(int rowOffset, int colOffset) {
        return new ChessPosition(this.row + rowOffset, this.col + colOffset);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition other = (ChessPosition) obj;
        return this.row == other.row && this.col == other.col;
    }
    public int hashCode() {
        return 31* row + col;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "ROW=" + row +
                ", COL=" + col +
                '}';
    }
}
