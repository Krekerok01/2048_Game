package game2048;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;

    public int score = 0;
    public int maxTile = 0;

    public Model() {
        resetGameTiles();
    }

    public void resetGameTiles(){
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles[0].length; i++){
            for (int j = 0; j < gameTiles[1].length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();
    }

    private void addTile(){
        List<Tile> emptyTiles = getEmptyTiles();
        if(!emptyTiles.isEmpty()){
            Tile ex = emptyTiles.get((int) (emptyTiles.size() * Math.random()));

            int newSize = Math.random() < 0.9 ? 2 : 4;
            ex.value = newSize;
        }
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < gameTiles[0].length; i++){
            for (int j = 0; j < gameTiles[1].length; j++) {
                if (gameTiles[i][j].value == 0){
                    list.add(gameTiles[i][j]);
                }
            }
        }
        return list;
    }
    private boolean compressTiles(Tile[] tiles){
        ArrayList<Integer> list = new ArrayList<>();
        boolean isChanged = false;

        for (Tile tile1: tiles){
            if (tile1.value != 0){
                list.add(tile1.value);
            }
        }

        for (Tile tile2: tiles){
            if (tile2.value == 0){
                list.add(tile2.value);
            }
        }

        for(int i = 0; i < tiles.length; i++) {
            if(tiles[i].value != list.get(i)){
                isChanged = true;
            }
        }

        for(int i = 0; i < tiles.length; i++) {
            tiles[i].value = list.get(i);
        }

        return isChanged;
    }

    private boolean mergeTiles(Tile[] tiles){
        boolean isChanged = false;

        for (int i = 0; i < tiles.length - 1; i++){
            if(tiles[i].isEmpty())
                continue;

            if (tiles[i].value == tiles[i + 1].value){
                tiles[i].value += tiles[i].value;
                tiles[i + 1].value = 0;
                isChanged = true;
                score += tiles[i].value;
                if (tiles[i].value > maxTile){
                    maxTile = tiles[i].value;
                }
            }
        }

        if(isChanged){
            compressTiles(tiles);
        }

        return isChanged;
    }

    public void left(){
        if(isSaveNeeded){
            saveState(gameTiles);
        }
        boolean isChanged = false;

        for (int i = 0; i < FIELD_WIDTH; i++){
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isChanged = true;
            }
        }

        if(isChanged) {
            addTile();
        }
        isSaveNeeded = true;
    }

    private void rotate(){
        Tile[][] tempArray = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++ ){
            for (int j = 0; j < FIELD_WIDTH; j++){
                tempArray[j][gameTiles.length - 1 - i] = gameTiles[i][j];
            }
        }

        gameTiles = tempArray;
    }

    public void up(){
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    public void down(){
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }
    public void right(){
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public Tile[][] getGameTiles(){
        return gameTiles;
    }

    /////////////переделать
    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }

    private boolean isFull() {
        return getEmptyTilesCount() == 0;
    }

    boolean canMove() {
        if (!isFull()) {
            return true;
        }

        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                Tile t = gameTiles[x][y];
                if ((x < FIELD_WIDTH - 1 && t.value == gameTiles[x + 1][y].value)
                        || ((y < FIELD_WIDTH - 1) && t.value == gameTiles[x][y + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    private void saveState(Tile[][] tiles){
        Tile[][] saveTile = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++){
                saveTile[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(saveTile);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n){
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

    public boolean hasBoardChanged(){
        boolean isChanged = false;
        Tile[][] t = previousStates.peek();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if(gameTiles[i][j].value != t[i][j].value){
                    isChanged = true;
                }
            }
        }

        return isChanged;
    }

    public MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency moveEfficiency;
        move.move();
        if (!hasBoardChanged()){
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        } else {
            moveEfficiency = new MoveEfficiency(getEmptyTilesCount(), score, move);
        }
        rollback();

        return moveEfficiency;
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::left));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down));

        queue.poll().getMove().move();
    }
}

