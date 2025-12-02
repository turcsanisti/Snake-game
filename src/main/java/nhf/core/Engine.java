package nhf.core;

import java.awt.Point;
import java.awt.event.KeyEvent;

import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import nhf.gui.GamePanel.InGamePanel;

/**
 * Implements the main game logic
 */
public class Engine {

    private Map map;
    private Snake snake;

    private InGamePanel igp;

    private Timer timer;
    private Timer speedRevertTimer;
    private Timer invertRevertTimer;
    private Timer megaAppleRevertTimer;

    private int dx;
    private int dy;
    private int applesEaten;
    private int delay;

    private long powerUpExpiration;
    private long start;

    private boolean directionChangedThisTick;
    private boolean powerUpGenerated;
    private boolean inverted = false;
    private boolean isMegaAppleOn = false;
    private boolean isCurrentAppleMega = false;
    private boolean gameWon = false;

    private Random random;
    
    private ObjectType storedPowerUp;

    /**
     * Constructor, sets up the engine: new map, new snake, initial direction etc.
     * @param igp
     */
    public Engine(InGamePanel igp) {
        map = new Map();
        snake = new Snake();

        this.igp = igp;

        this.igp.setMap(map);
        this.igp.setSnake(snake);

        dx = -1;
        dy = 0;
        applesEaten = 0;
        delay = 500;

        powerUpExpiration = 0;
        start = System.currentTimeMillis();

        directionChangedThisTick = false;
        powerUpGenerated = false;

        storedPowerUp = null;

        timer = new Timer(delay, e -> loop());
        random = new Random();

        generateApple();
    }

    /**
     * Ends the game with either a win or a loss message. Gives the user options to start a new game, return to menu, save their score or exit from the game.
     * @param message
     */
    public void gameEnded(String message) {
        timer.stop();
        
        Object[] options = {"New Game", "Return to Menu", "Save Score","Exit"};
        int o = JOptionPane.showOptionDialog(igp, message + " Score: " + applesEaten, "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (message.equals("You won!")) {
            gameWon = true;
        }

        switch (o) {
            case 0:
                resetGame();
                break;
            case 1:
                igp.returnToMenu();
                break;
            case 2:
                saveScore();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.exit(0);
                break;
        }
    }

    /**
     * Resets the game to its initial state
     */
    public void resetGame() {
        snake = new Snake();
        igp.setSnake(snake);
        map = new Map();
        igp.setMap(map);

        applesEaten = 0;
        dx = -1;
        dy = 0;
        
        timer.setDelay(delay);
        random = new Random();
        
        powerUpGenerated = false;
        directionChangedThisTick = false;
        inverted = false;
        gameWon = false;
        
        if (speedRevertTimer != null) {
            speedRevertTimer.stop();
            speedRevertTimer = null;
        }
        if (invertRevertTimer != null) {
            invertRevertTimer.stop();
            invertRevertTimer = null;
        }
        if (megaAppleRevertTimer != null) {
            megaAppleRevertTimer.stop();
            megaAppleRevertTimer = null;
        }

        storedPowerUp = null;

        start = System.currentTimeMillis();

        generateApple();

        igp.repaint();

        timer.start();

        igp.requestFocusInWindow();
    }

    /**
     * Checks if the snake's head collides with its body. Skips the first point as it's the head itself
     * @param head
     * @param snake
     * @return wheter head collides with body
     */
    public boolean headInBody(Point head, Snake snake) {
        boolean afterHead = false;
        
        for(Point p : snake.getBody()) {
            if (head.equals(p) && !afterHead)
                afterHead = true;
            else if (head.equals(p))
                return true;
        }

        return false;
    }

    /**
     * Handles the logic when the snake eats an apple: increases size, updates score, checks win condition, generates new apple and power-up if needed
     */
    private void handleAppleEating() {
                snake.setSize(snake.getSize() + 1);

                int pointsToAdd = 1;
                
                if (isCurrentAppleMega) {
                    pointsToAdd = 4;
                }
                if (inverted) {
                    pointsToAdd = 10;
                }
                if (speedRevertTimer != null) {
                    pointsToAdd *= 2;
                }

                applesEaten += pointsToAdd;

                if (snake.getSize() >= Map.SIZE * Map.SIZE) {
                    gameEnded("You won!");
                    return;
                }

                generateApple();
                
                if (applesEaten % 11 == 0 && applesEaten != 0 && !powerUpGenerated) {
                    generatePowerUp();
                    powerUpGenerated = true;
                }
    }

    /**
     * Game loop, that is called on every tick
     */
    public void loop() {
        directionChangedThisTick = false;
        boolean willEatApple = false;
        
        Point head = snake.getHead();
        Point nextHead = new Point(head.x + dx, head.y + dy);
        
        Point apple = map.getApple();

        
        if (isEatingApple(nextHead, apple)) {
            willEatApple = true;
            handleAppleEating();
        }

        Point pLoc = map.getPowerUpLocation();
        if (pLoc != null && nextHead.equals(pLoc)) {
            ObjectType type = map.getPowerUpType();
            pickUpPowerUp(type);
        }

        if (storedPowerUp != null && System.currentTimeMillis() > powerUpExpiration) {
            storedPowerUp = null;
        }
        
        snake.move(dx, dy, willEatApple);
            
        if(nextHead.x < 0 || nextHead.y < 0 || nextHead.x > Map.SIZE - 1 || nextHead.y > Map.SIZE - 1 || headInBody(head, snake)) {
            gameEnded("Game over!");
            return;
        }
            
        igp.updateGameStatus(storedPowerUp, getTimeLeft(), applesEaten, getFormattedTime());
        
        igp.repaint();
    }

    /**
     * Gets the formatted time since the start of the game in mm:ss format
     */
    public String getFormattedTime() {
        long sec = (System.currentTimeMillis() - start) / 1000;
        long min = sec / 60;
        sec = sec % 60;

        return String.format("%02d:%02d", min, sec);
    }

    /**
     * Saves the score by asking the user for their name and storing it in the scores.json file
     */
    private void saveScore() {
        String name = JOptionPane.showInputDialog(igp, "Add meg a neved: ");
        if (name != null && !name.trim().isEmpty()) {

            Scores scores = new Scores();
            scores.saveScore(name, applesEaten, getFormattedTime(), gameWon ? "Win" : "Lose");

            JOptionPane.showMessageDialog(igp, "Mentés sikeres.");
            
            igp.returnToMenu();
        }
        else {
            JOptionPane.showMessageDialog(igp, "Érvénytelen név. A pontszám nem lett elmentve.");
            igp.returnToMenu();
        }
    }

    /**
     * Handles the logic when the snake picks up a power-up. Removes it from the map, stores it and sets expiration time
     */
    private void pickUpPowerUp(ObjectType type) {
        deleteObjectFromMap(type);

        storedPowerUp = type;

        powerUpExpiration = System.currentTimeMillis() + 10000;
    }

    /**
     * Removes the power-up from the map after being picked up
     * @param type
     */
    public void deleteObjectFromMap(ObjectType type) {
        switch (type) {
            case SPEED:
                map.setPowerUp(null, null);
                break;
            case INVERTED:
                map.setPowerUp(null, null);
                break;
            case MEGAAPPLE:
                map.setPowerUp(null, null);
                break;
            default:
                break;
        }

        powerUpGenerated = false;
    }

    /**
     * Determines what type of object the snake has eaten based on its next head position
     * @param nextHead
     * @return
     */
    public ObjectType getEatenObjectType(Point nextHead) {
        if(nextHead.equals(map.getApple())) {
            return ObjectType.APPLE;
        }
        else if(nextHead.equals(map.getPowerUpLocation()) && map.getPowerUpType() == ObjectType.INVERTED) {
            return ObjectType.INVERTED;
        }
        else if(nextHead.equals(map.getPowerUpLocation()) && map.getPowerUpType() == ObjectType.SPEED) {
            return ObjectType.SPEED;
        }
        else
            return ObjectType.MEGAAPPLE;
    }

    /**
     * Generates a power-up at a random position on the map that is free (there is no snake body part or apple there)
     */
    public void generatePowerUp() {
        int x = -1;
        int y = -1;

        do {
            x = random.nextInt(Map.SIZE);
            y = random.nextInt(Map.SIZE);
        }
        while(isOccupied(x, y));

        int randomPowerUp = random.nextInt(3);
        
        if (randomPowerUp == 0) {
            map.setPowerUp(new Point(x, y), ObjectType.SPEED);
        }
        else if (randomPowerUp == 1) {
            map.setPowerUp(new Point(x, y), ObjectType.INVERTED);
        }
        else {
            map.setPowerUp(new Point(x, y), ObjectType.MEGAAPPLE);
        }
    }

    /**
     * Generates a new apple at a random position on the map that is free (there is no snake body part or power-up there)
     */
    public void generateApple() {
        int x = -1;
        int y = -1;

        isCurrentAppleMega = isMegaAppleOn;

        int bound = isMegaAppleOn ? (Map.SIZE - 1) : Map.SIZE;

        int attempts = 0;
        boolean found = false;

        while (attempts < 100) {
            x = random.nextInt(bound);
            y = random.nextInt(bound);

            if (validApple(x, y)) {
                found = true;
                break;
            }
            attempts++;
        }

        if (!found) {
            int[] coords = findValidSquare(bound, found);
            x = coords[0];
            y = coords[1];
        }

        if (found) {
            map.setApple(new Point(x, y));
        }
    }

    /**
     * Finds a valid square for the apple to spawn in if random generation fails
     * @param bound
     * @param found
     * @return the valid square coordinates
     */
    private int[] findValidSquare(int bound, boolean found) {
        int x = -1;
        int y = -1;

        for (int i = 0; i < bound; i++) {
                for (int j = 0; j < bound; j++) {
                    if (validApple(i, j)) {
                        x = i;
                        y = j;
                        found = true;
                        break;
                    }
                }
                if (found)
                    return new int[] {x, y};
            }

        return new int[] {x, y};
    }

    /**
     * Checks if the apple can be placed at the given coordinates
     * @param x
     * @param y
     * @return wheter the apple can be placed there
     */
    private boolean validApple(int x, int y) {
        if (isOccupied(x, y))
            return false;

        if (isCurrentAppleMega) {
            if (isOccupied(x + 1, y))
                return false;
            if (isOccupied(x, y + 1))
                return false;
            if (isOccupied(x + 1, y + 1))
                return false;
        }
        return true;
    }

    /**
     * Starts the game loop timer
     */
    public void start() {
        timer.start();
    }

    /**
     * Changes the snake's direction based on user input, considers inverted controls if the power-up is active
     * @param keyCode
     */
    public void changeDirection(int keyCode) {
        if (directionChangedThisTick) return;
        
        if (inverted) {
            keyCode = setInvertedKeys(keyCode);
        }
        
        boolean changed = false;

        if (keyCode == KeyEvent.VK_A && dx != 1) {
            dy = 0;
            dx = -1;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_D && dx != -1) {
            dy = 0;
            dx = 1;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_W && dy != 1) {
            dy = -1;
            dx = 0;
            changed = true;
        }
        else if (keyCode == KeyEvent.VK_S && dy != -1) {
            dy = 1;
            dx = 0;
            changed = true;
        }

        if (changed) directionChangedThisTick = true;
    }

    /**
     * Sets the inverted controls based on the key pressed
     * @param keyCode
     * @return the modified key code
     */
    private int setInvertedKeys(int keyCode) {
        if (keyCode == KeyEvent.VK_A) {
            keyCode = KeyEvent.VK_D;
        }
        else if (keyCode == KeyEvent.VK_D) {
            keyCode = KeyEvent.VK_A;
        } 
        else if (keyCode == KeyEvent.VK_W) {
            keyCode = KeyEvent.VK_S;
        }
        else if (keyCode == KeyEvent.VK_S) {
            keyCode = KeyEvent.VK_W;
        }

        return keyCode;
    }

    /**
     * Checks if a given position on the map is occupied by the snake, apple or power-up
     * @param x
     * @param y
     * @return wheter the position is occupied
     */
    public boolean isOccupied(int x, int y) {
        Point p = new Point(x, y);

        if (snake.getBody().contains(p))
            return true;

        if (map.getApple() != null && map.getApple().equals(p))
            return true;

        if (map.getPowerUpLocation() != null && map.getPowerUpLocation().equals(p))
            return true;
        return false;
    }

    /**
     * Activates the stored power-up and applies its effect
     */
    public void activatePowerUp() {
        if (storedPowerUp == null)
            return;

        switch (storedPowerUp) {
            case SPEED:
                speedUp();
                break;
             case INVERTED:
                invertKeys();
                break;
             case MEGAAPPLE:
                if(canMegaAppleSpawn())
                    setMegaApples();
                break;
            default:
                break;
        }

        storedPowerUp = null;
    }

    /**
     * Speeds up the game by halving the timer delay for a limited time
     */
    private void speedUp() {
        timer.setDelay(delay / 2);

        speedRevertTimer = startTimer(speedRevertTimer, () -> {
            timer.setDelay(delay);
        });
    }

    /**
     * Inverts the snake's controls for a limited time
     */
    private void invertKeys() {
        inverted = true;

        invertRevertTimer = startTimer(invertRevertTimer, () -> {
            inverted = false;
        });
    }

    /**
     * Enables mega apple mode for a limited time
     */
    private void setMegaApples() {
        isMegaAppleOn = true;

        megaAppleRevertTimer = startTimer(megaAppleRevertTimer, () -> {
            isMegaAppleOn = false;
        });
    }

    /**
     * Gets the currently stored power-up
     * @return the stored power-up 
     */
    public ObjectType getStoredPowerUp() {
        return storedPowerUp;
    }

    /**
     * Gets the time left for the currently stored power-up
     * @return the time left in seconds
     */
    public int getTimeLeft() {
        if (storedPowerUp == null) 
            return 0;
        long timeLeftMillis = powerUpExpiration - System.currentTimeMillis();
        return (int) Math.max(0, timeLeftMillis / 1000);
    }
    
    /**
     * Starts or restarts a timer for power-up effects
     * @param existingTimer
     * @param onFinish
     * @return the new timer
     */
    private Timer startTimer(Timer existingTimer, Runnable onFinish) {

        if (existingTimer != null && existingTimer.isRunning()) 
            existingTimer.stop();
        
        Timer newTimer = new Timer(8000, e -> {
            onFinish.run();
            ((Timer)e.getSource()).stop();
        });

        newTimer.setRepeats(false);
        newTimer.start();

        return newTimer;
    }

    /**
     * Checks if the snake is eating the apple
     * @param nextHead
     * @param apple
     * @return wheter the snake is eating the apple
     */
    private boolean isEatingApple(Point nextHead, Point apple) {
        if (apple == null)
            return false;

        if(isCurrentAppleMega) {
            boolean xHit = (nextHead.x == apple.x) || (nextHead.x == apple.x + 1);
            boolean yHit = (nextHead.y == apple.y) || (nextHead.y == apple.y + 1);
            return xHit && yHit;
        }
        else {
            return nextHead.equals(apple);
        }
    }
    
    /**
     * Checks if mega apple mode is currently active
     * @return wheter mega apple mode is on
     */
    public boolean isMegaAppleModeOn() {
        return isMegaAppleOn;
    }

    /**
     * Checks if a mega apple can spawn on the current map
     * @return wheter a mega apple can spawn
     */
    private boolean canMegaAppleSpawn() {
        for (int x = 0; x < Map.SIZE - 1; x++) {
            for (int y = 0; y < Map.SIZE - 1; y++) {
                if(!isOccupied(x, y)) {
                    boolean right = !isOccupied(x + 1, y);
                    boolean down = !isOccupied(x, y + 1);
                    boolean diagonal = !isOccupied(x + 1, y + 1);

                    if (right && down && diagonal)
                        return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if the current apple is mega
     * @return wheter the current apple is mega
     */
    public boolean currentAppleMega() {
        return isCurrentAppleMega;
    }
}
