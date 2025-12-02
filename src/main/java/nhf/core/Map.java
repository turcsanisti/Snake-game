package nhf.core;

import java.awt.Point;

/**
 * The game map represented as a 2 dimensional grid.
 */
public class Map {
    public static final int SIZE = 11;
    private int[][] grid = new int[SIZE][SIZE];
    private Point apple;
    private Point powerUp;
    private ObjectType powerUpType;

    public Map() {
        for(int row = 0; row < SIZE; row++) {
            for(int col = 0; col < SIZE; col++) {
                if((row + col) % 2 == 0) {
                    grid[row][col] = 1;
                }
                else
                    grid[row][col] = 0;
            }
        }
        apple = null;
    }

    public Point getApple() {
        return apple;
    }
    public Point getPowerUpLocation() {
        return powerUp;
    }
    
    public ObjectType getPowerUpType() {
        return powerUpType;
    }
    
    public void setApple(Point p) {
        apple = p;
    }
    public void setPowerUp (Point p, ObjectType ot) {
        powerUp = p;
        powerUpType = ot;
    }

    public int[][] getGrid() {
        return grid;
    }
}
