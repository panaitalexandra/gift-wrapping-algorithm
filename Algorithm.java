import java.util.ArrayList;
import java.util.TreeSet;
// ------------------------------------------------------------Jarvis Alg
public class Algorithm {
    private ArrayList<Point> points;
    private ArrayList<Point> northList, southList;
    private Point currentPoint, lastPoint, candidatePoint, testPoint;

    public Algorithm(ArrayList<Point> p) {
        this.points = new ArrayList<>(p);
        this.currentPoint = new Point(0, 0);
        this.lastPoint = new Point(0, 0);
        this.candidatePoint = new Point(0, 0);
        this.testPoint = new Point(0, 0);
        this.northList = new ArrayList<>();
        this.southList = new ArrayList<>();
    }

    private void sort() {
        TreeSet<Point> set = new TreeSet<>(points);
        points = new ArrayList<>(set);
    }

    public void execute() {
        // 0. Preprocessing
        // Sort points by abscissa (X coordinate)
        sort();

        boolean[] nodeVisited = new boolean[points.size()];
        if (points.isEmpty()) return;
        int i = 0;

        // 1. Choosing starting point
        currentPoint = points.get(0);
        lastPoint = points.get(points.size() - 1);

        nodeVisited[0] = true;
        northList.add(currentPoint);
        candidatePoint = currentPoint;
        System.out.println("Processing North List...");

        // 2. Find next point by selection
        // First part - north
        while (candidatePoint.compareTo(lastPoint) != 0) {
            // Until we did not reach the last point ->
            // Choosing all possible candidates only from the right of the current point
            for (int j = i + 1; j < points.size(); ++j) {
                boolean ok;
                candidatePoint = points.get(j);
                nodeVisited[j] = true;
                ok = true;

                // Validation canditate point by comparing with other test
                for (int k = i + 1; k < points.size(); ++k) {
                    testPoint = points.get(k);
                    if (!nodeVisited[k])
                        // alg's key
                        if (det() > 0) {
                            ok = false;
                            break;
                        }
                }
                // candidate validate -> update
                if (ok) {
                    northList.add(candidatePoint);
                    currentPoint = candidatePoint;
                    for (int l = i + 1; l < j; ++l)
                        nodeVisited[l] = true;
                    i = j;
                    break;
                } else
                    // candidate not validate
                    nodeVisited[j] = false;
            }

        }

        System.out.println("Processing South List...");
        i = 0;
        nodeVisited = new boolean[points.size()]; // Reset visited status

        currentPoint = points.get(0);
        lastPoint = points.get(points.size() - 1);
        nodeVisited[0] = true;
        southList.add(currentPoint);
        candidatePoint = currentPoint;

        // 3. Find next point by selection
        // First part - south
        while (candidatePoint.compareTo(lastPoint) != 0) {
            // Until we did not reach the last point ->
            for (int j = i + 1; j < points.size(); ++j) {
                boolean ok;
                candidatePoint = points.get(j);
                nodeVisited[j] = true;
                ok = true;

                for (int k = i + 1; k < points.size(); ++k) {
                    testPoint = points.get(k);
                    if (!nodeVisited[k])
                        if (det() < 0) {
                            ok = false;
                            break;
                        }

                }

                if (ok) {
                    southList.add(candidatePoint);
                    currentPoint = candidatePoint;
                    for (int l = i + 1; l < j; ++l) nodeVisited[l] = true;
                    i = j;
                    break;
                } else
                    nodeVisited[j] = false;
            }
        }
    }

    public ArrayList<Point> getNorthList() {
        System.out.println(northList.size() + " Size of North List");
        return northList;
    }

    public ArrayList<Point> getSouthList() {
        System.out.println(southList.size() + " Size of South List");
        return southList;
    }

    public double det() {
        return testPoint.getX() * currentPoint.getY() +
                currentPoint.getX() * candidatePoint.getY() +
                candidatePoint.getX() * testPoint.getY() -
                currentPoint.getY() * candidatePoint.getX() -
                candidatePoint.getY() * testPoint.getX() -
                testPoint.getY() * currentPoint.getX();
    }
}