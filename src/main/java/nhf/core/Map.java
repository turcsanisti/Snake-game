package nhf.core;

import java.awt.Point;

public class Map {
    public int[][] map = new int[11][11];
    Point apple;

    public Map() {
        for(int row = 0; row < 11; row++) {
            for(int col = 0; col < 11; col++) {
                if((row + col) % 2 == 0) {
                    map[row][col] = 1;
                }
                else
                    map[row][col] = 0;
            }
        }
        apple = new Point();
    }

    public Point getApple() {
        return apple;
    }

    public int[][] getGrid() {
        return map;
    }
}
