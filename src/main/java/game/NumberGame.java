package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberGame {

    private int score;
    private final int height;
    private final int width;
    protected ArrayList<ArrayList<Tile>> field;

    public NumberGame (int width, int height) {
        if (width < 1 || height < 1) throw new IllegalArgumentException("Width and height must not be smaller than 1.");
        this.width = width; //x Coord
        this.height = height; //y Coord
        this.score = 0;
        field = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            field.add(new ArrayList<>());
            for (int j = 0; j < height; j++) {
                field.get(i).add(j, null);
            }
        }
    }

    public NumberGame (int width, int height, int initialTiles) {
        if (width < 1 || height < 1) throw new IllegalArgumentException("Width and height must not be smaller than 1.");
        if (initialTiles < 0 || height*width < initialTiles) throw new IllegalArgumentException("InitialTiles must be >= 0 and <= width*height.");
        this.height = height;
        this.width = width;
        this.score = 0;
        field = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            field.add(new ArrayList<>());
            for (int j = 0; j < height; j++) {
                field.get(i).add(j, null);
            }
        }
        for (int i = 0; i < initialTiles; i++) {
            addRandomTile();
        }
    }

    public int get(Coordinate2D coord) {
        return get(coord.getX(), coord.getY());
    }

    public int get(int x, int y) {
        if (x < 0 || width <= x || y < 0 || height <= y) throw new IndexOutOfBoundsException();
        return field.get(x).get(y) == null ? 0 : field.get(x).get(y).getValue();
    }

    public int getPoints(){
        return score;
    }

    public boolean isFull(){
        for (ArrayList<Tile> row : field) {
            for (Tile t : row) {
                if (t == null) return false;
            }
        }
        return true;
    }

    public Tile addRandomTile() {
        if (isFull()) throw new TileExistsException("The field is full.");
        List<Coordinate2D> freeTile = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (get(x,y) == 0) freeTile.add(new Coordinate2D(x,y));
            }
        }
        Random rnd = new Random();
        Coordinate2D choose = freeTile.get(rnd.nextInt(freeTile.size()));
        return addTile(choose.getX(), choose.getY(), rnd.nextInt(10) == 0 ? 4 : 2);
    }

    public Tile addTile (int x, int y, int value){
        if (x >= width || y >= height) throw new IndexOutOfBoundsException();
        if (get(x,y) != 0) throw new TileExistsException();
        Tile toAdd = new Tile(new Coordinate2D(x,y),value);
        field.get(x).remove(y);
        field.get(x).add(y, toAdd);
        return toAdd;
    }

    public List<Move> move(Direction dir) {
        boolean row = true; //Indicates if the move is operated on rows. Means RIGHT and LEFT (TRUE) or UP and DOWN (FALSE) move.
        boolean asc = true; //Indicates if the move is operated with the natural numeration of the grid. Means LEFT and UP (TRUE) or RIGHT and DOWN (FALSE) moves.
        switch (dir){
            case DOWN:
                asc = false;
                break;
            case LEFT:
                row = false;
                break;
            case RIGHT:
                asc = false;
                row = false;
                break;
            default:
                break;
        }
        List<Move> re = new ArrayList<>();

        for (int ii = 0; ii < width; ii++) {
            int moveToSpace = asc ? 0 : height - 1; //When moving a tile or merging two tiles indicates in which spot the tile will be moved.
            for (int jj = 0; jj < height; jj++) {
                //Changes j depending if you go UP or DOWN
                int j = asc ? jj : height - 1 - jj;
                int i = ii;
                int l = ii; //For swapping i and k.
                //Swap i,j through dummy's when move RIGHT or LEFT
                if (!row){
                    int temp = j;
                    j = i;
                    i = temp;
                }
                //Only try moving Tiles, when it is not a dummy tile.
                if (get(i,j) != 0) {
                    int k = (row ? j : i) + (asc ? +1 : -1);
                    if (!row){
                        int temp = k;
                        k = l;
                        l = temp;
                    }
                    boolean mergedTiles = false;
                    boolean nextTileViableToMerge = true;
                    //Scans next Tiles
                    while (!mergedTiles && nextTileViableToMerge && (((row ? k : l) < height) && asc || ((row ? k : l) >= 0) && !asc)) {
                        //When merging to tiles
                        if (get(l,k) != get(i,j) && get(l,k) != 0) nextTileViableToMerge = false;
                        if (get(l,k) == get(i,j)) {
                            //Adding Move, when first tile of merging would move also
                            Move tileMove = null;
                            if ((moveToSpace < (row ? j : i)) && asc || (moveToSpace > (row ? j : i)) && !asc) {
                                if (row) {
                                    tileMove = new Move(new Coordinate2D(i,j),new Coordinate2D(i, moveToSpace), get(i,j), get(i,j));
                                } else {
                                    tileMove = new Move(new Coordinate2D(i,j),new Coordinate2D(moveToSpace, j), get(i,j), get(i,j));
                                }
                                moveTileInFiled(tileMove);
                                re.add(tileMove);
                            }
                            if (row) {
                                tileMove = new Move(new Coordinate2D(l, k), new Coordinate2D(i, moveToSpace), get(l,k), get(l,k)*2);
                            } else {
                                tileMove = new Move(new Coordinate2D(l, k), new Coordinate2D(moveToSpace, j), get(l,k), get(l,k)*2);
                            }
                            moveTileInFiled(tileMove);
                            re.add(tileMove);
                            score += tileMove.getNewValue();
                            mergedTiles = true;
                        }
                        if (row) {
                            k = asc ? k + 1 : k - 1;
                        } else {
                            l = asc ? l + 1 : l - 1;
                        }
                    }
                    //move Tile when there wasn't a tile merge and can be moved.
                    if (!mergedTiles && ((moveToSpace < (row ? j : i)) && asc || (moveToSpace > (row ? j : i)) && !asc)) {
                        Move tileMove = null;
                        if (row) {
                            tileMove = new Move(new Coordinate2D(i, j), new Coordinate2D(i, moveToSpace), get(i,j), get(i,j));
                        } else {
                            tileMove = new Move(new Coordinate2D(i, j), new Coordinate2D(moveToSpace, j), get(i,j), get(i,j));
                        }
                        moveTileInFiled(tileMove);
                        re.add(tileMove);
                    }
                    moveToSpace = asc ? moveToSpace+1 : moveToSpace-1;
                }
            }
        }
        return re;
    }

    private void moveTileInFiled(Move move) {
        //Remove old Tile and replace for dummy tile
        field.get(move.getFrom().getX()).remove(move.getFrom().getY());
        field.get(move.getFrom().getX()).add(move.getFrom().getY(), null);
        //Adding new Tile
        field.get(move.getTo().getX()).remove(move.getTo().getY());
        field.get(move.getTo().getX()).add(move.getTo().getY(), new Tile(move.getTo(), move.getNewValue()));
        //Move Debug code
        /*
        System.out.println("Temp printout after Move: "+move.toString());
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(get(j,i)+" ");
            }
            System.out.print(" | "+i+"\n");
        }
        System.out.println("_________|");
        System.out.println("0 1 2 3");
         */
        }


    public boolean canMove(Direction dir){
        //System.out.println("\nTest can move: ");
        ArrayList<ArrayList<Tile>> fieldCopy = new ArrayList<>();
        int scoreCopy = score;

        for (ArrayList<Tile> at : field) {
            ArrayList<Tile> temp = new ArrayList<>();
            for (Tile t : at) {
                temp.add(cloneTile(t));
            }
            fieldCopy.add(temp);
        }

        int countMoves = move(dir).size();
        score = scoreCopy;
        field = fieldCopy;
        //System.out.println("\nTest can move over.\n");
        if (countMoves > 0) {
            return true;
        }
        return false;
    }

    private Tile cloneTile(Tile toClone) {
        return toClone == null ? null : new Tile(new Coordinate2D(toClone.getCoordinate().getX(), toClone.getCoordinate().getY()), toClone.getValue());
    }

    public boolean canMove() {
        return canMove(Direction.RIGHT) || canMove(Direction.UP) || canMove(Direction.LEFT) || canMove(Direction.DOWN);
    }
}
