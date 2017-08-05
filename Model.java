
import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    protected int score;
    protected int maxTile;
    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        resetGameTiles();
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].getValue() == 0) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }

        return emptyTiles;
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();

        if (!emptyTiles.isEmpty()) {
            int randomTileIndex = (int) (Math.random() * emptyTiles.size());
            emptyTiles.get(randomTileIndex).value = (Math.random() < 0.9) ? 2 : 4;
        }
    }

    protected void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();

        score = 0;
        maxTile = 2;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean result = false;
        Tile[] tiles1 = tiles.clone();

        Arrays.sort(tiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                int result;

                if (o1.getValue() != 0 && o2.getValue() != 0) {
                    result = 0;
                }
                else {
                    result = Integer.compare(o2.getValue(), o1.getValue());
                }

                return result;
            }
        });

        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].equals(tiles1[i])) {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isMerges = false;

        for (int j = 0; j < tiles.length - 1; j++) {
            if (tiles[j].getValue() == tiles[j + 1].getValue() && !tiles[j].isEmpty() && !tiles[j + 1].isEmpty()) {
                tiles[j].setValue(tiles[j].getValue() * 2);
                tiles[j + 1].setValue(0);
                isMerges = true;

                score += tiles[j].getValue();
                maxTile = maxTile > tiles[j].getValue() ? maxTile : tiles[j].getValue();
            }
        }

        compressTiles(tiles);
        return isMerges;
    }

    public void left() {
        boolean isChange = false;

        if (isSaveNeeded) {
            saveState(gameTiles);
        }

        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChange = true;
            }
        }

        if (isChange) {
            addTile();
        }

        isSaveNeeded = true;
    }

    public void right() {
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public void down() {
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    public void up() {
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    private void rotate() {
        for (int i = 0; i < FIELD_WIDTH / 2; i++) {
            for (int j = i; j < FIELD_WIDTH - i - 1; j++) {
                Tile tmp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[j][FIELD_WIDTH - i - 1];
                gameTiles[j][FIELD_WIDTH - i - 1] = gameTiles[FIELD_WIDTH - i - 1][FIELD_WIDTH - j - 1];
                gameTiles[FIELD_WIDTH - i - 1][FIELD_WIDTH - j - 1] = gameTiles[FIELD_WIDTH - j - 1][i];
                gameTiles[FIELD_WIDTH - j - 1][i] = tmp;
            }
        }
    }

    public boolean canMove() {
        boolean haveMove = true;

        if (getEmptyTiles().isEmpty()) {
            haveMove = false;
            metka: for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    if (j < FIELD_WIDTH - 1) {
                        if (gameTiles[i][j].getValue() == gameTiles[i][j + 1].getValue()) {
                            haveMove = true;
                            break metka;
                        }
                    }
                    if (i > 0) {
                        if(gameTiles[i][j].getValue() == gameTiles[i - 1][j].getValue()) {
                            haveMove = true;
                            break metka;
                        }
                    }
                }
            }
        }

        return haveMove;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] newGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                newGameTiles[i][j] = new Tile(tiles[i][j].getValue());
            }
        }
        previousStates.push(newGameTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;

        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    private boolean hasBoardChanged() {
        boolean result = false;

        int currentSum = 0;
        int lastMoveSum = 0;

        if (!previousStates.isEmpty()) {
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    currentSum += gameTiles[i][j].getValue();
                    lastMoveSum += previousStates.peek()[i][j].getValue();
                }
            }
        }

        if (currentSum != lastMoveSum) {
            result = true;
        }

        return result;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;

        move.move();

        if(hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }

        rollback();
        
        return moveEfficiency;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());

        queue.offer(getMoveEfficiency(this::left));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down));

        queue.poll().getMove().move();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

}
