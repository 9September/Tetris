import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

public class Board extends JPanel implements ActionListener {

    final int BoardWidth = 10;
    final int BoardHeight = 22;
    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusBar;
    Shape curPiece;
    Shape nextPiece;
    Tetrominoes[] board;
    NextPiecePanel nextPiecePanel;
    RankingPanel rankingPanel;
    List<ScoreEntry> scores;
    Main main;

    public Board(Main main) {
        this.main = main;
        setFocusable(true);
        curPiece = new Shape();
        nextPiece = new Shape();
        nextPiece.setRandomShape();
        timer = new Timer(400, this);
        timer.start();
        statusBar = main.getStatusbar();
        scores = main.getScores();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void setNextPiecePanel(NextPiecePanel nextPiecePanel) {
        this.nextPiecePanel = nextPiecePanel;
    }

    public void setRankingPanel(RankingPanel rankingPanel) {
        this.rankingPanel = rankingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    int squareSize() {
        return Math.min(getSize().width / BoardWidth, getSize().height / BoardHeight);
    }

    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }

    public void start() {
        if (isPaused) {
            return;
        }
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusBar.setText("paused");
        } else {
            timer.start();
            statusBar.setText("SCORE: " + numLinesRemoved);
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int squareSize = squareSize();
        int boardWidth = squareSize * BoardWidth;
        int boardHeight = squareSize * BoardHeight;
        int boardX = (getWidth() - boardWidth) / 2;
        int boardY = (getHeight() - boardHeight) / 2;

        drawGrid(g, boardX, boardY, squareSize); // 그리드 라인 그리기

        for (int i = 0; i < BoardHeight; i++) {
            for (int j = 0; j < BoardWidth; j++) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape) {
                    drawSquare(g, boardX + j * squareSize, boardY + i * squareSize, shape, squareSize);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, boardX + x * squareSize, boardY + (BoardHeight - y - 1) * squareSize, curPiece.getShape(), squareSize);
            }
        }
    }

    private void drawGrid(Graphics g, int boardX, int boardY, int squareSize) {
        g.setColor(new Color(200, 200, 200)); // 연한 회색
        for (int i = 0; i <= BoardHeight; i++) {
            g.drawLine(boardX, boardY + i * squareSize, boardX + BoardWidth * squareSize, boardY + i * squareSize);
        }
        for (int j = 0; j <= BoardWidth; j++) {
            g.drawLine(boardX + j * squareSize, boardY, boardX + j * squareSize, boardY + BoardHeight * squareSize);
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; i++) {
            board[i] = Tetrominoes.NoShape;
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        curPiece = nextPiece;
        curX = BoardWidth / 2;
        curY = BoardHeight - 1 + curPiece.minY();

        nextPiece = new Shape();
        nextPiece.setRandomShape();

        if (nextPiecePanel != null) {
            nextPiecePanel.setNextPiece(nextPiece);
            nextPiecePanel.repaint();
        }

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            gameOver();
        }
    }

    private void gameOver() {
        String name = JOptionPane.showInputDialog(main.getFrame(), "Game Over! Enter your name:", "Game Over", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            scores.add(new ScoreEntry(name, numLinesRemoved));
            scores.sort((s1, s2) -> s2.getScore() - s1.getScore());
            if (rankingPanel != null) {
                rankingPanel.updateScores();
            }
        }
        int option = JOptionPane.showConfirmDialog(main.getFrame(), "Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            start();
        } else {
            System.exit(0);
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight) {
                return false;
            }
            if (shapeAt(x, y) != Tetrominoes.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; j++) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; k++) {
                    for (int j = 0; j < BoardWidth; j++) {
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines * 100; // 줄당 100점 추가
            statusBar.setText("SCORE: " + numLinesRemoved);
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape, int squareSize) {
        Color colors[] = {new Color(0, 0, 0), new Color(101, 243, 68), new Color(238, 192, 56),
                new Color(241, 26, 26), new Color(187, 36, 215), new Color(25, 38, 112),
                new Color(54, 79, 245), new Color(248, 145, 65)};

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareSize - 2, squareSize - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareSize - 1, x, y);
        g.drawLine(x, y, x + squareSize - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareSize - 1, x + squareSize - 1, y + squareSize - 1);
        g.drawLine(x + squareSize - 1, y + squareSize - 1, x + squareSize - 1, y + 1);
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused) {
                return;
            }

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_Z:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_D:
                    oneLineDown();
                    break;
            }
        }
    }
}
