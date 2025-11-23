package nhf.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import nhf.core.Engine;
import nhf.core.Map;
import nhf.core.Snake;

public class GamePanel extends JFrame{
    
    public GamePanel() {
        super("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        
        Image bg = new ImageIcon(getClass().getResource("/background.png")).getImage();

        MenuPanel mp = new MenuPanel(bg);
        mp.setLayout(new BoxLayout(mp, BoxLayout.Y_AXIS));
        
        Image normalPlay = new ImageIcon(getClass().getResource("/playbutton_normal.png")).getImage();
        Image normalScores = new ImageIcon(getClass().getResource("/scoresbutton_normal.png")).getImage();
        Image normalExit = new ImageIcon(getClass().getResource("/exitbutton_normal.png")).getImage();
        
        Image hoveredPlay = new ImageIcon(getClass().getResource("/playbutton_hovered.png")).getImage();
        /*ImageIcon pressed = new ImageIcon(getClass().getResource("/button_pressed.png")); */
        JButton playButton = new JButton();
        /*playButton.setPressedIcon(pressed);*/
        JButton scoresButton = new JButton();
        JButton exitButton = new JButton();
        
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setFocusPainted(false);
        playButton.setOpaque(false);
        
        scoresButton.setBorderPainted(false);
        scoresButton.setContentAreaFilled(false);
        scoresButton.setFocusPainted(false);
        scoresButton.setOpaque(false);
        
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false);
        exitButton.setOpaque(false);
        
        playButton.setAlignmentX(CENTER_ALIGNMENT);
        scoresButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.setAlignmentX(CENTER_ALIGNMENT);
        
        mp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = (int)(mp.getWidth() * 0.2);
                int heigth = (int)(mp.getHeight() * 0.1);
                
                Image playScaled = normalPlay.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                Image scoresScaled = normalScores.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                Image exitScaled = normalExit.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
                
                ImageIcon playHoverScaled = new ImageIcon(hoveredPlay.getScaledInstance(width, heigth, Image.SCALE_SMOOTH));
                
                playButton.setIcon(new ImageIcon(playScaled));
                scoresButton.setIcon(new ImageIcon(scoresScaled));
                exitButton.setIcon(new ImageIcon(exitScaled));
                
                playButton.setRolloverIcon(playHoverScaled);

                revalidate();
                repaint();
            }
            
        });
        
        playButton.addActionListener(e -> {
            InGamePanel igp = new InGamePanel();
            Engine eng = new Engine(igp);

            igp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    eng.changeDirection(e.getKeyCode());
                }
            });

            igp.setFocusable(true);

            setContentPane(igp);
            revalidate();
            repaint();    
            igp.requestFocusInWindow();

            eng.start();
        });
            
        
        
        mp.add(Box.createVerticalGlue());
        mp.add(playButton);
        mp.add(scoresButton);
        mp.add(exitButton);
        mp.add(Box.createVerticalGlue());
        
        setContentPane(mp);
        
    }
    
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

    public class InGamePanel extends JPanel {
        private transient Map map;
        private transient Snake snake;

        public void setSnake(Snake snake) {
            this.snake = snake;
        }
        public void setMap(Map map) {
            this.map = map;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            int[][] grid = map.getGrid();
            
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            int cellSize = Math.min(panelHeight, panelWidth) / 11;

            int offsetX = (panelWidth - (11 * cellSize)) / 2;
            int offsetY = (panelHeight - (11 * cellSize)) / 2;

            for(int row = 0; row < 11; row++) {
                for(int col = 0; col < 11; col++) {
                    int x = offsetX + col * cellSize;
                    int y = offsetY + row * cellSize;

                    int cell = grid[row][col];
                    if (cell == 0) {
                        g.setColor(Color.BLUE);
                    }
                    else if(cell == 1){
                        g.setColor(Color.CYAN);
                    }

                    g.fillRect(x, y, cellSize, cellSize);
                    
                }
            }

            Point apple = map.getApple();
            if (apple != null) {
                int appleX = offsetX + (apple.x * cellSize);
                int appleY = offsetY + (apple.y * cellSize);

                g.setColor(Color.RED);
                g.fillRect(appleX, appleY, cellSize, cellSize);
            }

            for (Point p : snake.getBody()) {
                int snakePixelX = offsetX + (p.x * cellSize);
                int snakePixelY = offsetY + (p.y * cellSize);

                g.setColor(Color.GREEN);

                g.fillRect(snakePixelX, snakePixelY, cellSize, cellSize);
            }

            Point head = snake.getHead();
            int headX = offsetX + (head.x * cellSize);
            int headY = offsetY + (head.y * cellSize);

            g.setColor(new Color(0, 120, 0));
            g.fillRect(headX, headY, cellSize, cellSize);
        }
    }
}
