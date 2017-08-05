
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int result;

        result = Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);

        if (result == 0) {
            result = Integer.compare(this.score, o.score);
        }

        return result;
    }

    public Move getMove() {
        return move;
    }

}
