package game;

public class Tile {

    private final Coordinate2D coord;
    private final int value;

    public Tile (Coordinate2D coord, int value) {
        if (value < 1) throw new IllegalArgumentException("Value must not be smaller then 1.");
        this.value = value;
        this.coord = coord;
    }

    public Coordinate2D getCoordinate() {
        return coord;
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != Tile.class) return false;
        Tile oCast = (Tile) o;
        return oCast.getCoordinate().equals(getCoordinate()) && oCast.getValue() == getValue();
    }

    public int hashCode() {
        return (getCoordinate().toString()+"123"+getValue()).hashCode();
    }
}
