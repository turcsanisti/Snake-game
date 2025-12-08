package nhf.gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import nhf.core.Engine;
import nhf.core.Map;
import nhf.core.Snake;
import nhf.core.ObjectType;

/**
 * The main game panel that handles the menu and in-game display
 */
public class GamePanel extends JFrame {
    private Image bg;
    private Image scoresBg;

    private Image normalPlay;
    private Image normalScores;
    private Image normalExit;
    private Image hoveredPlay;
    private Image hoveredScores;
    private Image hoveredExit;

    /**
     * Constructs the GamePanel and initializes the menu
     */
    public GamePanel() {
        super("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        bg = new ImageIcon(getClass().getResource("/background.png")).getImage();
        scoresBg = new ImageIcon(getClass().getResource("/scores_background.png")).getImage();

        normalPlay = new ImageIcon(getClass().getResource("/playbutton_normal.png")).getImage();
        normalScores = new ImageIcon(getClass().getResource("/scoresbutton_normal.png")).getImage();
        normalExit = new ImageIcon(getClass().getResource("/exitbutton_normal.png")).getImage();
        hoveredPlay = new ImageIcon(getClass().getResource("/playbutton_hovered.png")).getImage();
        hoveredScores = new ImageIcon(getClass().getResource("/scoresbutton_hovered.png")).getImage();
        hoveredExit = new ImageIcon(getClass().getResource("/exitbutton_hovered.png")).getImage();

        menu();
    }

    /**
     * Sets up and displays the main menu
     */
    public void menu() {
        MenuPanel mp = new MenuPanel(bg);
        mp.setLayout(new BoxLayout(mp, BoxLayout.Y_AXIS));

        JButton playButton = new JButton();
        JButton scoresButton = new JButton();
        JButton exitButton = new JButton();

        setStyle(playButton);
        setStyle(scoresButton);
        setStyle(exitButton);

        // Resizes buttons when the window is resized
        mp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = (int)(mp.getWidth() * 0.2);
                int heigth = (int)(mp.getHeight() * 0.1);
                
                Image playScaled = normalPlay.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                Image scoresScaled = normalScores.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                Image exitScaled = normalExit.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                
                ImageIcon playHoverScaled = new ImageIcon(hoveredPlay.getScaledInstance(width, heigth, Image.SCALE_SMOOTH));
                ImageIcon scoresHoverScaled = new ImageIcon(hoveredScores.getScaledInstance(width, heigth, Image.SCALE_SMOOTH));
                ImageIcon exitHoverScaled = new ImageIcon(hoveredExit.getScaledInstance(width, heigth, Image.SCALE_SMOOTH));
                
                playButton.setIcon(new ImageIcon(playScaled));
                scoresButton.setIcon(new ImageIcon(scoresScaled));
                exitButton.setIcon(new ImageIcon(exitScaled));
                
                playButton.setRolloverIcon(playHoverScaled);
                scoresButton.setRolloverIcon(scoresHoverScaled);
                exitButton.setRolloverIcon(exitHoverScaled);

                revalidate();
                repaint();
            }
            
        });
        
        playButton.addActionListener(e -> {
            InGamePanel igp = new InGamePanel();
            Engine eng = new Engine(igp);

            igp.setEngine(eng);

            igp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                        eng.activatePowerUp();
                    }
                    else {
                        eng.changeDirection(e.getKeyCode());
                    }
                }
            });

            igp.setFocusable(true);

            setContentPane(igp);
            revalidate();
            repaint();    
            igp.requestFocusInWindow();

            eng.start();
        });

        scoresButton.addActionListener(e -> {
            ScorePanel sp = new ScorePanel(scoresBg, this);

            setContentPane(sp);
            revalidate();
            repaint();
        });

        exitButton.addActionListener(e -> System.exit(0));

        mp.add(Box.createVerticalGlue());
        mp.add(playButton);
        mp.add(scoresButton);
        mp.add(exitButton);
        mp.add(Box.createVerticalGlue());

        setContentPane(mp);
        revalidate();
        repaint();
    }
    
    /**
     * Sets the style for the menu buttons
     * @param button
     */
    private void setStyle(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFocusable(false);
        button.setAlignmentX(CENTER_ALIGNMENT);
        
    }
    
    /**
     * The panel displayed in the main menu
     */
    public class MenuPanel extends JPanel {
        private transient Image backgroundImg;
        
        public MenuPanel(Image backgroundImg) {
            this.backgroundImg = backgroundImg;
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * The panel displayed during the game
     */
    public class InGamePanel extends JPanel {
        private transient Map map;
        private transient Snake snake;
        private transient Engine engine;

        private ObjectType storedItem;

        private int timeRemaining;
        private int score = 0;
        private int currentDx = 0;
        private int currentDy = 0;

        private String elapsedTime = "00:00";

        public void setSnake(Snake snake) {
            this.snake = snake;
        }
        public void setMap(Map map) {
            this.map = map;
        }
        public void setEngine(Engine engine) {
            this.engine = engine;
        }
        
        /**
         * Updates the game status display
         * @param item
         * @param time
         * @param score
         * @param elapsedTime
         */
        public void updateGameStatus(ObjectType item, int time, int score, String elapsedTime, int dx, int dy) {
            storedItem = item;
            timeRemaining = time;
            this.score = score;
            this.elapsedTime = elapsedTime;
            currentDx = dx;
            currentDy = dy;
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (map == null || snake == null)
                return;

            int[][] grid = map.getGrid();
            
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            int cellSize = Math.min(panelHeight, panelWidth) / Map.SIZE;

            int offsetX = (panelWidth - (Map.SIZE * cellSize)) / 2;
            int offsetY = (panelHeight - (Map.SIZE * cellSize)) / 2;

            paintGrid(g, offsetX, offsetY, cellSize, grid);

            Point apple = map.getApple();
            paintApple(apple, offsetX, offsetY, cellSize, g);

            Point pLoc = map.getPowerUpLocation();
            ObjectType pType = map.getPowerUpType();
            paintPowerUps(pLoc, offsetX, offsetY, cellSize, g, pType);

            List<Point> body = snake.getBody();
            drawBody(offsetX, offsetY, cellSize, g, body);

            Point head = snake.getHead();
            drawHead(g, offsetX, offsetY, cellSize, head);
            
            drawInventory(g);
            drawScore(g);
            drawTimer(g);
        }

        /**
         * Paints the grid in the backrground of the game
         * @param g
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param grid
         */
        private void paintGrid(Graphics g, int offsetX, int offsetY, int cellSize, int[][] grid) {
            for(int row = 0; row < Map.SIZE; row++) {
                for(int col = 0; col < Map.SIZE; col++) {
                    int x = offsetX + col * cellSize;
                    int y = offsetY + row * cellSize;

                    int cell = grid[row][col];
                    if (cell == 0) {
                        g.setColor(new Color(215, 192, 151));
                    }
                    else if(cell == 1){
                        g.setColor(new Color(231, 222, 175));
                    }

                    g.fillRect(x, y, cellSize, cellSize);
                }
            }
        }

        /**
         * Paints the apple on the background
         * @param apple
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param g
         */
        private void paintApple(Point apple, int offsetX, int offsetY, int cellSize, Graphics g) {
            if (apple != null) {
                boolean isMega = false;
                if (engine != null) {
                    isMega = engine.currentAppleMega();
                }

                int size = isMega ? cellSize * 2 : cellSize;
                g.setColor(Color.RED);
                int applePixelX = offsetX + (apple.x * cellSize);
                int applePixelY = offsetY + (apple.y * cellSize);
                g.fillRect(applePixelX, applePixelY, size, size);
            }
        }

        /**
         * Paints the power-ups on the background
         * @param pLoc
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param g
         * @param pType
         */
        private void paintPowerUps(Point pLoc, int offsetX, int offsetY, int cellSize, Graphics g, ObjectType pType) {
            if (pLoc != null && pType != null) {
                Color c = Color.WHITE;
                switch (pType) {
                    case SPEED:
                        c = Color.MAGENTA;
                        break;
                    case INVERTED:
                        break;
                    case MEGAAPPLE:
                        c = Color.DARK_GRAY;
                        break;
                    default:
                        break;
                }
                paintObjects(pLoc, offsetX, offsetY, cellSize, g, c);
            }
        }

        /**
         * Paints the snake's body. The tail is drawn differently, with rounded edges, so it can be distinguished from the rest of the body
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param g
         * @param body
         */
        private void drawBody(int offsetX, int offsetY, int cellSize, Graphics g, List<Point> body) {
            for (int i = 0; i < body.size(); i++) {
                Point p = body.get(i);
                int px = offsetX + (p.x * cellSize);
                int py = offsetY + (p.y * cellSize);

                g.setColor(new Color(115, 175, 111));

                if (i == body.size() - 1 && body.size() > 1) {
                    Point neighbor = body.get(i - 1);

                    g.fillOval(px, py, cellSize, cellSize);

                    if (neighbor.x < p.x) {
                        g.fillRect(px, py, cellSize / 2, cellSize);
                    } else if (neighbor.x > p.x) {
                        g.fillRect(px + cellSize / 2, py, cellSize / 2, cellSize);
                    } else if (neighbor.y < p.y) {
                        g.fillRect(px, py, cellSize, cellSize / 2);
                    } else if (neighbor.y > p.y) {
                        g.fillRect(px, py + cellSize / 2, cellSize, cellSize / 2);
                    }
                    
                    g.setColor(Color.BLACK);
                    g.drawOval(px, py, cellSize, cellSize);

                } else {
                    g.fillRect(px, py, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(px, py, cellSize, cellSize);
                }
            }
        }

        /**
         * Draws the snake's head with eyes and a tongue
         * @param g
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param head
         */
        private void drawHead(Graphics g, int offsetX, int offsetY, int cellSize, Point head) {
            if (head != null) {
                int headX = offsetX + (head.x * cellSize);
                int headY = offsetY + (head.y * cellSize);


                g.setColor(new Color(0, 126, 110));
                g.fillRect(headX, headY, cellSize, cellSize);

                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setStroke(new java.awt.BasicStroke(3));
                
                int eyeSize = cellSize / 5;
                Color tongueColor = Color.RED;

                if (currentDx == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(headX + cellSize / 2, headY + cellSize / 4, eyeSize, eyeSize);
                    g.fillOval(headX + cellSize / 2, headY + 3 * cellSize / 4 - eyeSize, eyeSize, eyeSize);
                    g.setColor(tongueColor);
                    g2d.drawLine(headX + cellSize, headY + cellSize / 2, headX + cellSize + 5, headY + cellSize / 2);
            
                } else if (currentDx == -1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(headX + cellSize / 4, headY + cellSize / 4, eyeSize, eyeSize);
                    g.fillOval(headX + cellSize / 4, headY + 3 * cellSize / 4 - eyeSize, eyeSize, eyeSize);
                    g.setColor(tongueColor);
                    g2d.drawLine(headX, headY + cellSize / 2, headX - 5, headY + cellSize / 2);

                } else if (currentDy == -1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(headX + cellSize / 4, headY + cellSize / 4, eyeSize, eyeSize);
                    g.fillOval(headX + 3 * cellSize / 4 - eyeSize, headY + cellSize / 4, eyeSize, eyeSize);
                    g.setColor(tongueColor);
                    g2d.drawLine(headX + cellSize / 2, headY, headX + cellSize / 2, headY - 5);

                } else if (currentDy == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(headX + cellSize / 4, headY + cellSize / 2, eyeSize, eyeSize);
                    g.fillOval(headX + 3 * cellSize / 4 - eyeSize, headY + cellSize / 2, eyeSize, eyeSize);
                    g.setColor(tongueColor);
                    g2d.drawLine(headX + cellSize / 2, headY + cellSize, headX + cellSize / 2, headY + cellSize + 5);
                }
                
                g2d.setStroke(new java.awt.BasicStroke(1));
            }
        }
        /**
         * Paints game objects like power-ups
         * @param object
         * @param offsetX
         * @param offsetY
         * @param cellSize
         * @param g
         * @param color
         */
        public void paintObjects(Point object, int offsetX, int offsetY, int cellSize, Graphics g, Color color) {
                if (object != null) {
                    int speedPowerUpX = offsetX + (object.x * cellSize);
                    int speedPowerUpY = offsetY + (object.y * cellSize);
    
                    g.setColor(color);
                    g.fillRect(speedPowerUpX, speedPowerUpY, cellSize, cellSize);
                }
        }

        /**
         * Draws the "inventory"
         * @param g
         */
        private void drawInventory(Graphics g) {
            if (storedItem != null) {
                g.setColor(Color.BLACK);

                String txt;
                Color itemColor = Color.BLACK;
                switch (storedItem) {
                    case SPEED:
                        itemColor = Color.MAGENTA;
                        txt = "SPEED UP: " + timeRemaining + "s (USE SPACE TO ACTIVATE)";
                        g.drawString(txt, 20, 30);
                        break;
                    case INVERTED:
                        itemColor = Color.WHITE;
                        txt = "INVERT CONTROLS: " + timeRemaining + "s (USE SPACE TO ACTIVATE)";
                        g.drawString(txt, 20, 30);
                        break;
                    case MEGAAPPLE:
                        itemColor = Color.DARK_GRAY;
                        txt = "MEGA APPLES!: " + timeRemaining + "s (USE SPACE TO ACTIVATE)";
                        g.drawString(txt, 20, 30);
                        break;
                
                    default:
                        break;
                }

                g.setColor(itemColor);
                g.fillRect(20, 40, 30, 30);
                g.setColor(Color.BLACK);
                g.drawRect(20, 40, 30, 30);
            }
        }

        /**
         * Draws the current score
         * @param g
         */
        private void drawScore(Graphics g) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));

            String txt = "Score: " + score;

            int txtWidth = g.getFontMetrics().stringWidth(txt);
            int x = getWidth() - txtWidth - 20;
            int y = 50;

            g.drawString(txt, x, y);
        }

        /**
         * Draws the elapsed time
         * @param g
         */
        private void drawTimer(Graphics g) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));

            String txt = "Time elapsed: " + elapsedTime;

            int txtWidth = g.getFontMetrics().stringWidth(txt);
            int x = getWidth() - txtWidth;
            int y = 100;

            g.drawString(txt, x, y);
        }

        public void returnToMenu() {
            GamePanel.this.menu();
        }
    }

}
