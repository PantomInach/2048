package game;

public class Move {

    private final Coordinate2D from;
    private final Coordinate2D to;
    private final int oldValue;
    private final int newValue;

    public Move (Coordinate2D from, Coordinate2D to, int oldValue, int newValue) {
        if (oldValue < 1 || newValue < 1) throw new IllegalArgumentException("OldValue and newValue must not be smaller then 1.");
        this.from = from;
        this.to = to;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public Move (Coordinate2D from, Coordinate2D to, int value) {
        if (value < 1) throw new IllegalArgumentException("Value must not be smaller then 1.");
        this.from = from;
        this.to = to;
        this.oldValue = value;
        this.newValue = value;
    }

    public Coordinate2D getTo() {
        return to;
    }

    public Coordinate2D getFrom() {
        return from;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public boolean isMerge() {
        return !(getNewValue()==getOldValue());
    }

    @Override
    public String toString() {
        return getFrom().toString()+" = "+getOldValue()+" -> "+getTo().toString()+" = "+getNewValue()+""+(isMerge() ? " (M)":"");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
       if (o.getClass() != Move.class) return false;
       Move oCast = (Move) o;
       return getTo().equals(oCast.getTo()) && getFrom().equals(oCast.getFrom()) && getOldValue() == oCast.getOldValue() && getNewValue() == oCast.getNewValue();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
