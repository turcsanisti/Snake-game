package nhf.core;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * The snake controlled by the player
 */
public class Snake {
    /**
     * The body of the snake represented as a list of points
     */
    private List<Point> body;

    public Snake() {
        body = new LinkedList<>();
        body.add(new Point(Map.SIZE / 2, Map.SIZE / 2));
    }

    /**
     * Moves the snake in the given direction, growing it if an apple was eaten
     * @param dx
     * @param dy
     * @param appleEaten
     */
    public void move(int dx, int dy, boolean appleEaten) {
        body.addFirst(new Point(getHead().x+dx, getHead().y+dy));
        if(!appleEaten) body.removeLast();
    }

    public void increaseSize(int dx, int dy, boolean appleEaten) {
        move(dx, dy, appleEaten);
    }

    public Point getHead() {
        return body.getFirst();
    }
    public List<Point> getBody() {
        return body;
    }
    public int getSize() {
        return body.size();
    }
}
