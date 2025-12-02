package nhf.gui; // Fontos: ugyanaz a csomag, mint a GamePanel!

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

// Fontos: importálni kell a Scores osztályt a másik csomagból!
import nhf.core.Scores; 

public class ScorePanel extends JPanel {
    private Image background;
    private GamePanel parentFrame;

    public ScorePanel(Image bg, GamePanel parentFrame) {
        this.background = bg;
        this.parentFrame = parentFrame;
        
        // BorderLayout, hogy kitöltse az ablakot
        setLayout(new BorderLayout());

        // 1. CÍM
        JLabel titleLabel = new JLabel("TOPLISTA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE); // Fehér betű
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 2. ADATOK BETÖLTÉSE
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

        // 3. TÁBLÁZAT LÉTREHOZÁSA
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // --- STÍLUS BEÁLLÍTÁSOK ---
        
        // Táblázat formázása
        table.setFont(new Font("Monospaced", Font.BOLD, 18));
        table.setRowHeight(30);
        table.setForeground(Color.WHITE); // Fehér szöveg
        
        // Átlátszóság
        table.setOpaque(false);
        table.setBackground(new Color(0, 0, 0, 0)); // Teljesen átlátszó
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        // Fejléc (Header)
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 0, 0, 150)); // Félig átlátszó fekete
        header.setForeground(Color.YELLOW);
        
        // 4. GÖRGETŐSÁV
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // A szöveg mögötti félig átlátszó háttér
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 100));

        // Margó
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        // 5. VISSZA GOMB
        JButton backButton = new JButton("VISSZA A MENÜBE");
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