package nhf.core;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.Random;

import javax.swing.JOptionPane;
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
    private boolean directionChangedThisTick;
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
        directionChangedThisTick = false;

        timer = new Timer(350, e -> loop());
        random = new Random();

        generateApple();
    }

    public void gameOver() {
        timer.stop();
        
        Object[] options = {"New Game", "Exit"};
        int o = JOptionPane.showOptionDialog(igp, "vesztettem", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (o) {
            case 0:
                resetGame();
                break;
            case 1:
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
    }

    public void resetGame() {
        snake = new Snake();

        igp.setSnake(snake);

        dx = -1;
        dy = 0;
        directionChangedThisTick = false;

        timer = new Timer(350, e -> loop());
        random = new Random();

        generateApple();

        igp.repaint();

        timer.start();

        igp.requestFocusInWindow();
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
        
        directionChangedThisTick = false;
        
        Point head = snake.getHead();
        Point nextHead = new Point(head.x + dx, head.y + dy);
        boolean willEatApple = false;
        Point apple = map.getApple();
        
        if (apple != null && nextHead.equals(apple)) {
            willEatApple = true;
            generateApple();
        }
        
        snake.move(dx, dy, willEatApple);
            
        if(nextHead.x < 0 || nextHead.y < 0 || nextHead.x > 10 || nextHead.y > 10 || headInBody(head, snake)) {
            gameOver();
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
        if (directionChangedThisTick) return;

        boolean changed = false;
        if (keyCode == KeyEvent.VK_LEFT && dx != 1) {
            dy = 0;
            dx = -1;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_RIGHT && dx != -1) {
            dy = 0;
            dx = 1;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_UP && dy != 1) {
            dy = -1;
            dx = 0;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_DOWN && dy != -1) {
            dy = 1;
            dx = 0;
            changed = true;
        }

        if (changed) directionChangedThisTick = true;
    }
}
