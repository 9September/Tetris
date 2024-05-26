import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    private JFrame frame;
    private JLabel statusbar;
    private List<ScoreEntry> scores;

    public Main() {
        frame = new JFrame("Tetris");

        statusbar = new JLabel("SCORE: 0");
        frame.add(statusbar, BorderLayout.SOUTH); // JLabel을 JFrame에 추가

        scores = new ArrayList<>();

        Board board = new Board(this);
        NextPiecePanel nextPiecePanel = new NextPiecePanel(board);
        RankingPanel rankingPanel = new RankingPanel(scores);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(nextPiecePanel, BorderLayout.NORTH);
        rightPanel.add(rankingPanel, BorderLayout.CENTER);

        frame.add(board, BorderLayout.CENTER); // Board를 JFrame에 추가
        frame.add(rightPanel, BorderLayout.EAST); // 오른쪽 패널을 JFrame에 추가

        board.setNextPiecePanel(nextPiecePanel);
        board.setRankingPanel(rankingPanel);

        board.start();

        frame.setSize(800, 800); // JFrame의 크기 설정
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE); // JFrame의 종료 동작 설정
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JLabel getStatusbar() {
        return statusbar;
    }

    public List<ScoreEntry> getScores() {
        return scores;
    }

    public JFrame getFrame() {
        return frame;
    }

    public static void main(String[] args) {
        new Main();
    }
}
