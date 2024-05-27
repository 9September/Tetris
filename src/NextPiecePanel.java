import java.awt.*;
import javax.swing.*;

public class NextPiecePanel extends JPanel {

    private Shape nextPiece;

    public NextPiecePanel(Board board) {
        setPreferredSize(new Dimension(200, 250));
        setBorder(BorderFactory.createTitledBorder("Next Piece"));
    }

    public void setNextPiece(Shape nextPiece) {
        this.nextPiece = nextPiece;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nextPiece != null) {
            int squareSize = Math.min(getWidth() / 5, getHeight() / 5);
            int startX = (getWidth() - (nextPiece.getWidth() * squareSize)) / 2;
            int startY = (getHeight() - (nextPiece.getHeight() * squareSize)) / 2 + squareSize;

            for (int i = 0; i < 4; i++) {
                int x = nextPiece.x(i);
                int y = nextPiece.y(i);
                drawSquare(g, startX + x * squareSize, startY + y * squareSize, nextPiece.getShape(), squareSize);
            }
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
}
