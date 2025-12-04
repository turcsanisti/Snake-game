package nhf.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import nhf.core.Scores; 
    /**
     * A panel that displays the high score table with a background image
     */
public class ScorePanel extends JPanel {
    private Image background;
    private GamePanel parentFrame;
    
    /**
     * Creates a ScorePanel with the given background image and parent frame
     * @param bg
     * @param parentFrame
     */
    public ScorePanel(Image bg, GamePanel parentFrame) {
        this.background = bg;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("TOPLIST", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        Scores scoresManager = new Scores();
        List<Scores.Entry> scores = scoresManager.getTopScores();

        String[] columnNames = {"RANKING", "PLAYER", "POINTS", "TIME", "RESULT"};
        Object[][] data = new Object[scores.size()][5];

        for (int i = 0; i < scores.size(); i++) {
            Scores.Entry e = scores.get(i);
            data[i][0] = (i + 1) + ".";
            data[i][1] = e.getName();
            data[i][2] = e.getScore();
            data[i][3] = e.getTime();
            data[i][4] = e.getResult();
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table.setFont(new Font("Monospaced", Font.BOLD, 18));
        table.setRowHeight(30);
        table.setForeground(Color.WHITE);

        table.setOpaque(false);
        table.setBackground(new Color(0, 0, 0, 0));
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 0, 0, 150));
        header.setForeground(Color.YELLOW);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 100));

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        JButton backButton = new JButton("RETURN TO MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusable(false);
        
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(true);
        backButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        backButton.addActionListener(e -> {
            this.parentFrame.menu(); 
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}