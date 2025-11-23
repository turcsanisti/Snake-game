package nhf.core;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.Random;

import javax.swing.Timer;

import nhf.gui.GamePanel;
import nhf.gui.GamePanel.InGamePanel;

public class Engine {
    private Map map;
    private Snake snake;
    private InGamePanel igp;
    private Timer timer;
    private int dx;
    private int dy;
    private Random random;

    /**
     * Fel: dy = -1
     * Le: dy = 1
     * Balra: dx = -1
     * Jobbra: dx = 1
     * (0,0) bal felső sarok
     * Default irány: balra
     */

    public Engine(InGamePanel igp) {
        this.igp = igp;

        map = new Map();
        snake = new Snake();

        this.igp.setSnake(snake);
        this.igp.setMap(map);

        dx = -1;
        dy = 0;

        timer = new Timer(500, e -> loop());
        random = new Random();

        generateApple();
    }

    public static void gameOver() {
        System.out.println("Game over!");
    }

    public  boolean headInBody(Point head, Snake snake) {
        boolean afterHead = false;
        
        for(Point p : snake.getBody()) {
            if (head.equals(p) && !afterHead)
                afterHead = true;
            else if (head.equals(p))
                return true;
        }

        return false;
    }

    public void loop() {
        
        Point head = snake.getHead();
        Point nextHead = new Point(head.x + dx, head.y + dy);
        boolean willEatApple = false;
        Point apple = map.getApple();
        
        if (apple != null && nextHead.equals(apple)) {
            willEatApple = true;
            generateApple();
        }
        
        snake.move(dx, dy, willEatApple);
            
        if(head.x < 0 || head.y < 0 || head.x > 10 || head.y > 10 || headInBody(head, snake)) {
            gameOver();
            timer.stop();
            return;
        }
            
        igp.repaint();
    }

    public void generateApple() {
            int x = -1;
            int y = -1;
            do {
        
                x = random.nextInt(11);
                y = random.nextInt(11);
            }
            while(appleWouldBeOnBody(x, y));

            map.apple = new Point(x, y);
    }

    public boolean appleWouldBeOnBody(int x, int y) {
        return snake.getBody().contains(new Point(x, y));
    }

    public void start() {
        timer.start();
    }

    public void changeDirection(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT && dx != 1) {
            dy = 0;
            dx = -1;
        }
        else if (keyCode == KeyEvent.VK_RIGHT && dx != -1) {
            dy = 0;
            dx = 1;
        }
        else if (keyCode == KeyEvent.VK_UP && dy != 1) {
            dy = -1;
            dx = 0;
        }
        if (keyCode == KeyEvent.VK_DOWN && dy != -1) {
            dy = 1;
            dx = 0;
        }
    }
}
