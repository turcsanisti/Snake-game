package nhf.core;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    // a kígyó egy pontokat ((x,y) koordinátákat) tároló láncolt lista, getFirst() -> feje
    private List<Point> body;
    private int size;

    public Snake() {
        body = new LinkedList<>();
        body.add(new Point(Map.SIZE / 2, Map.SIZE / 2));
        size = 1;
    }
    // mozgatás: a kígyónak új feje lesz, a farka törlődik
    public void move(int dx, int dy, boolean appleEaten) {
        body.addFirst(new Point(getHead().x+dx, getHead().y+dy));
        if(!appleEaten) body.removeLast();
    }

    public void increaseSize(int dx, int dy, boolean appleEaten) {
        // megnöveljük a kígyó testét = hozzáadunk egy elemet a láncolt listához
        move(dx, dy, appleEaten);
    }

    public Point getHead() {
        return body.getFirst();
    }
    public List<Point> getBody() {
        return body;
    }
    public int getSize() {
        return size;
    }

    public void setSize(int i) {
        size = i;
    }
}
