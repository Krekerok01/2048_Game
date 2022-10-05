package game2048.view;

import game2048.controller.Controller;
import game2048.entity.Tile;
import game2048.model.Model;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 96;
    private static final int TILE_MARGIN = 12;
    private boolean isGameWon = false;
    private boolean isGameLost = false;

    private Controller controller;

    public View(Controller controller) {
        setFocusable(true);
        this.controller = controller;
        addKeyListener(controller);
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    public void setGameWon(boolean gameWon) {
        isGameWon = gameWon;
    }

    public boolean isGameLost() {
        return isGameLost;
    }

    public void setGameLost(boolean gameLost) {
        isGameLost = gameLost;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                drawTile(g, controller.getGameTiles()[y][x], x, y);
            }
        }

        g.drawString("Score: " + controller.getScore(), 140, 465);

        if (isGameWon) {
            showMessage("You've won \uD83D\uDE0A");
        } else if (isGameLost) {
            showMessage("You've lost \uD83D\uDE22");
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        startNewGame();
    }

    private void startNewGame(){
        Model model = new Model();
        Controller controller = new Controller(model);
        JFrame game = new JFrame();

        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450, 520);
        game.setResizable(false);

        game.add(controller.getView());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        int value = tile.getValue();
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);

        Graphics2D g = setDataToGraphics2DAndGetIt((Graphics2D) g2, tile.getTileColor(), xOffset, yOffset, tile.getFontColor(), font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0)
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
    }

    private Graphics2D setDataToGraphics2DAndGetIt(Graphics2D g, Color tileColor, int xOffset, int yOffset, Color fontColor, Font font){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(tileColor);
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 8, 8);
        g.setColor(fontColor);
        g.setFont(font);
        return g;
    }

    private static int offsetCoors(int arg) {
        return arg * (TILE_MARGIN + TILE_SIZE) + TILE_MARGIN;
    }

}
