package game;

public class Coordinate2D {

    private final int x;
    private final int y;

    public Coordinate2D(int x, int y) {
        if (x < 0 || y < 0) throw new IllegalArgumentException("x and y must not be smaller then 0.");
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "("+x+","+y+")";
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != Coordinate2D.class) return false;
        Coordinate2D oCast = (Coordinate2D) o;
        if (oCast.getX() == getX() && oCast.getY() == getY()) return true;
        return  false;
    }

    public int hashCode(){
        return toString().hashCode();
    }
}
