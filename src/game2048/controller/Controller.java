package game2048.controller;

import game2048.model.Model;
import game2048.view.View;
import game2048.entity.Tile;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public Tile[][] getGameTiles(){
        return model.getGameTiles();
    }

    public int getScore(){
        return model.getScore();
    }

    public void resetGame(){
        model.setScore(0);
        view.setGameWon(false);
        view.setGameLost(false);
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        checkingForResetingGame(e);
        checkingForLoss();
        doStepIfItIsAvailable(e);
        checkingForWin();
        view.repaint();
    }

    private void checkingForResetingGame(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
            resetGame();
        }
    }



    private void checkingForLoss() {
        if (!model.canMove()){
            view.setGameLost(true);
        }
    }


    private void doStepIfItIsAvailable(KeyEvent e){
        if (!view.isGameLost() && !view.isGameWon()){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    model.left();
                    break;
                case KeyEvent.VK_RIGHT:
                    model.right();
                    break;
                case KeyEvent.VK_UP:
                    model.up();
                    break;
                case KeyEvent.VK_DOWN:
                    model.down();
                    break;
                case KeyEvent.VK_Z:
                    model.rollback();
                    break;
                case KeyEvent.VK_R:
                    model.randomMove();
                    break;
                case KeyEvent.VK_A:
                    model.autoMove();
                    break;
            }
        }
    }

    private void checkingForWin() {
        if (model.getMaxTile() == WINNING_TILE){
            view.setGameWon(true);
        }
    }



    public View getView() {
        return view;
    }

}
