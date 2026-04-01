import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class Drawing extends JPanel {
    private ArrayList<Point> points;
    private JTextArea logger;

    private int state; // 0 = Input, 1 = Finished, 2 = Animation


    private Timer animTimer;
    private ArrayList<Point> currentList; // Current list being processed (North or South)
    private boolean isDrawingNorth;       // Flag: drawing North or South?
    private int currentSegmentIndex;      // Which line segment we are currently on
    private float drawProgress;           // 0.0f -> 1.0f (percentage of current line drawn)
    private Runnable onAnimationFinish;   // Callback when animation ends

    private ArrayList<Point> cachedNorthList;
    private ArrayList<Point> cachedSouthList;

    private final Color GRID_COLOR = new Color(224, 224, 224);
    private final Color AXIS_COLOR = new Color(100, 100, 100);
    private final Color POINT_COLOR = new Color(248, 141, 141);
    private final Color HULL_NORTH_COLOR = new Color(140, 0, 96);
    private final Color HULL_SOUTH_COLOR = new Color(140, 0, 96);
    private final Stroke HULL_STROKE = new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public Drawing(JTextArea logger) {
        this.logger = logger;
        points = new ArrayList<>();
        state = 0;

        this.setPreferredSize(new Dimension(501, 501));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                processMouseClick(e);
            }
        });
    }


    public void startAnimation(Runnable onFinish) {
        if (points.size() < 3) return;

        this.onAnimationFinish = onFinish;

        Algorithm alg = new Algorithm(points);
        alg.execute();
        cachedNorthList = alg.getNorthList();
        cachedSouthList = alg.getSouthList();


        isDrawingNorth = true;
        currentList = cachedNorthList;
        currentSegmentIndex = 0;
        drawProgress = 0.0f;
        state = 2;


        if (animTimer != null && animTimer.isRunning())
            animTimer.stop();
        animTimer = new Timer(16, e -> updateAnimationFrame());
        animTimer.start();

    }

    private void updateAnimationFrame() {
        drawProgress += 0.04f;

        if (drawProgress >= 1.0f) {
            drawProgress = 0.0f;
            currentSegmentIndex++;

            if (currentSegmentIndex >= currentList.size() - 1) {
                if (isDrawingNorth) {

                    isDrawingNorth = false;
                    currentList = cachedSouthList;
                    currentSegmentIndex = 0;
                } else {
                    animTimer.stop();
                    state = 1;
                    if (onAnimationFinish != null)
                        onAnimationFinish.run();
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawAxes(g2);
        drawPoints(g2);

        if (points.size() >= 3) {
            if (state == 1) {
                if (cachedNorthList == null) { // Recalculate only if needed
                    Algorithm alg = new Algorithm(points);
                    alg.execute();
                    cachedNorthList = alg.getNorthList();
                    cachedSouthList = alg.getSouthList();
                }
                drawFullList(g2, cachedNorthList, HULL_NORTH_COLOR);
                drawFullList(g2, cachedSouthList, HULL_SOUTH_COLOR);
            }
            else
                if (state == 2)
                    drawAnimatedState(g2);
        }
    }

    // Draws the current state of the animation
    private void drawAnimatedState(Graphics2D g2) {
        g2.setStroke(HULL_STROKE);

        if (isDrawingNorth)
            drawPartialList(g2, cachedNorthList, HULL_NORTH_COLOR);
         else {
            drawFullList(g2, cachedNorthList, HULL_NORTH_COLOR);
            drawPartialList(g2, cachedSouthList, HULL_SOUTH_COLOR);
        }
    }


    private void drawPartialList(Graphics2D g2, ArrayList<Point> list, Color c) {
        if (list == null || list.size() < 2)
            return;
        g2.setColor(c);

        for (int i = 0; i < currentSegmentIndex; i++) {
            Point p1 = list.get(i);
            Point p2 = list.get(i+1);
            drawLineRescaled(g2, p1, p2);
            drawPointLabel(g2, p1, i);
        }

        if (currentSegmentIndex < list.size() - 1) {
            Point pStart = list.get(currentSegmentIndex);
            Point pEnd = list.get(currentSegmentIndex + 1);

            drawPointLabel(g2, pStart, currentSegmentIndex);

            double curX = pStart.getX() + (pEnd.getX() - pStart.getX()) * drawProgress;
            double curY = pStart.getY() + (pEnd.getY() - pStart.getY()) * drawProgress;

            int x1 = calcX(pStart.getX());
            int y1 = calcY(pStart.getY());
            int x2 = calcX(curX);
            int y2 = calcY(curY);

            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x2-3, y2-3, 6, 6);
        }
    }

    private void drawFullList(Graphics2D g2, ArrayList<Point> list, Color c) {
        if (list == null) return;
        g2.setColor(c);
        g2.setStroke(HULL_STROKE);

        for (int i = 0; i < list.size() - 1; i++) {
            drawLineRescaled(g2, list.get(i), list.get(i+1));
            drawPointLabel(g2, list.get(i), i);
        }
        if (!list.isEmpty())
            drawPointLabel(g2, list.get(list.size()-1), list.size()-1);

    }

    private void drawLineRescaled(Graphics2D g2, Point p1, Point p2) {
        g2.drawLine(calcX(p1.getX()), calcY(p1.getY()), calcX(p2.getX()), calcY(p2.getY()));
    }

    private void drawPointLabel(Graphics2D g2, Point p, int index) {
        Font originalFont = g2.getFont();
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(Color.BLACK);
        g2.drawString(String.valueOf(index), calcX(p.getX()) + 5, calcY(p.getY()) - 5);
        g2.setFont(originalFont);
        g2.setColor(isDrawingNorth ? HULL_NORTH_COLOR : HULL_SOUTH_COLOR); // Reset color
    }

    private void drawPoints(Graphics2D g2) {
        for (Point p : points) {
            int px = calcX(p.getX());
            int py = calcY(p.getY());
            g2.setColor(new Color(0,0,0, 50)); // Shadow
            g2.fillOval(px - 1, py - 1, 10, 10);
            g2.setColor(POINT_COLOR);
            g2.fillOval(px - 4, py - 4, 8, 8);
        }
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1));
        for(int i = -25; i <= 25; i++) {
            g2.drawLine(calcX(i), 0, calcX(i), 500);
            g2.drawLine(0, calcY(i), 500, calcY(i));
        }
    }

    private void drawAxes(Graphics2D g2) {
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, 250, 500, 250);
        g2.drawString("X", 480, 240);
        g2.drawLine(250, 0, 250, 500);
        g2.drawString("Y", 260, 15);
    }

    public int calcX(double x) { return (int) (250 + x * 10); }
    public int calcY(double y) { return (int) (250 - y * 10); }
    public int getPointsCount() { return points.size(); }

    public void setState(int type) {
        this.state = type;
        if(type == 0) {
            cachedNorthList = null;
            cachedSouthList = null;
            if (animTimer != null) animTimer.stop();
        }
        repaint();
    }

    public void processMouseClick(MouseEvent e) {
        if (state == 0) {
            double Mx = Math.round(((e.getX() - 250) / 10.0) * 100.0) / 100.0;
            double My = Math.round(((250 - e.getY()) / 10.0) * 100.0) / 100.0;
            points.add(new Point(Mx, My));
            if(logger != null) logger.append("Point: [" + Mx + ", " + My + "]\n");
            repaint();
        } else
            if(logger != null) logger.append("Reset to add more points.\n");
    }

    public void reset() {
        points.clear();
        state = 0;
        cachedNorthList = null;
        cachedSouthList = null;
        if(animTimer != null) animTimer.stop();
        repaint();
        if(logger != null) logger.append("\n----Canvas Reset----\n");
    }
}