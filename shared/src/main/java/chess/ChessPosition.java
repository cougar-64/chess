package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int ROW;
    private final int COL;

    public ChessPosition(int row, int col) {
        this.ROW = row;
        this.COL = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return ROW;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return COL;
    }

    public ChessPosition offset(int rowOffset, int colOffset) {
        return new ChessPosition(this.ROW + rowOffset, this.COL + colOffset);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition other = (ChessPosition) obj;
        return this.ROW == other.ROW && this.COL == other.COL;
    }
    public int hashCode() {
        return 31* ROW + COL;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "ROW=" + ROW +
                ", COL=" + COL +
                '}';
    }
}
