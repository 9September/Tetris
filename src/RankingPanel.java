import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RankingPanel extends JPanel {

    private List<ScoreEntry> scores;

    public RankingPanel(List<ScoreEntry> scores) {
        this.scores = scores;
        setPreferredSize(new Dimension(150, 400));
        setBorder(BorderFactory.createTitledBorder("Top Rankings"));
    }

    public void updateScores() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.BLACK);
        int y = 30;

        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            ScoreEntry score = scores.get(i);
            g.drawString("Top"+(i + 1) + " : " + score.getScore() + "score / " + score.getName(), 10, y);
            y += 20;
        }
    }
}
