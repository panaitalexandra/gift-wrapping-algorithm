import java.util.Comparator;

public class Point implements Comparable<Point> {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int compareTo(Point other) {
        // Sort by X coordinate
        if (other.getX() < this.getX()) return 1;
        if (other.getX() > this.getX()) return -1;

        // If X is equal, sort by Y coordinate
        if (other.getX() == this.getX()) {
            if (this.y > other.getY()) return 1;
            if (this.y < other.getY()) return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}