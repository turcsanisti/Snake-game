package nhf.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import nhf.core.Map;
import nhf.core.ObjectType;

public class MapTest {

    @Test
    public void testGridInitialization() {
        Map map = new Map();
        int[][] grid = map.getGrid();
        assertEquals(Map.SIZE, grid.length);
        assertEquals(Map.SIZE, grid[0].length);
    }

    @Test
    public void testSetAndGetApple() {
        Map map = new Map();
        Point p = new Point(3, 3);
        map.setApple(p);
        assertEquals(p, map.getApple());
    }

    @Test
    public void testSetAndGetPowerUp() {
        Map map = new Map();
        Point p = new Point(5, 5);
        ObjectType type = ObjectType.SPEED;
        map.setPowerUp(p, type);
        
        assertAll(
            () -> assertEquals(p, map.getPowerUpLocation()),
            () -> assertEquals(type, map.getPowerUpType())
        );
    }
}