package nhf.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import nhf.core.Map;
import nhf.core.Snake;

public class SnakeTest {

    @Test
    public void testInitialSize() {
        Snake snake = new Snake();
        assertEquals(1, snake.getSize());
    }

    @Test
    public void testInitialPosition() {
        Snake snake = new Snake();
        Point expected = new Point(Map.SIZE / 2, Map.SIZE / 2);
        assertEquals(expected, snake.getHead());
    }

    @Test
    public void testMoveUpdatesHead() {
        Snake snake = new Snake();
        Point initialHead = snake.getHead();
        snake.move(1, 0, false);
        Point newHead = snake.getHead();
        assertEquals(initialHead.x + 1, newHead.x);
        assertEquals(initialHead.y, newHead.y);
    }

    @Test
    public void testGrowthOnMove() {
        Snake snake = new Snake();
        snake.move(0, 1, true);
        assertEquals(2, snake.getSize());
    }
}