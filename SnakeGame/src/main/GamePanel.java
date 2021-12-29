package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class GamePanel extends JPanel implements ActionListener {

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 75;
    private int[] x = new int[GAME_UNITS];
    private int[] y = new int[GAME_UNITS];
    private int bodyParts = 5;
    private int appleX, appleY, applesEaten;
    private char direction = 'R';
    private boolean running = false;
    Timer timer;
    Random random;


    public GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapater());
        startGame();
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //Draw Apple
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        //Draw Body
        g.setColor(Color.ORANGE);
        g.fillOval(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
        g.setColor(Color.yellow);
        for(int i = 1; i < bodyParts; i++){
            g.drawOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        displayScore(g);

        if(!running) gameOver(g);
    }

    private void displayScore(Graphics g) {
        // Display the score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten * 100,
                (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten * 100))/2, SCREEN_HEIGHT - UNIT_SIZE);
    }

    public void newApple(){

        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        // Avoid apples in the body of the snake (the following code needs improvement)
        while(IntStream.of(x).anyMatch(num -> num == appleX) || IntStream.of(y).anyMatch(num -> num == appleY)){
            newApple();
        }
    }

    public void move(){
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'R' -> {
                x[0] += UNIT_SIZE;
            }
            case 'L' -> {
                x[0] -= UNIT_SIZE;
            }
            case 'U' -> {
                y[0] -= UNIT_SIZE;
            }
            case 'D' -> {
                y[0] += UNIT_SIZE;
            }
        }
    }

    public void checkApple(){
        if(x[0] == appleX && y[0] == appleY){
            bodyParts ++;
            applesEaten ++;
            newApple();
        }
    }

    public void checkCollision(){
        // Check if head of the snake collides with the body of the snake;
        for(int i = bodyParts; i > 0; i--){
            if (x[i] == x[0] && y[i] == y[0]) {
                running = false;
                break;
            }
        }

        // Check if the snake collides with the border.
        if(x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        // Display game over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT / 2);
        g.setFont(new Font("Times Roman", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Press Space to restart",
                (SCREEN_WIDTH - metrics.stringWidth("Press Space to restart"))/2, SCREEN_HEIGHT / 2 + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapater extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT -> {
                    if(direction != 'R'){
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if(direction != 'L'){
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if(direction != 'D'){
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if(direction != 'U'){
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_SPACE -> {
                    if(!running){
                        bodyParts = 5;
                        applesEaten = 0;
                        running = true;
                        x = new int[GAME_UNITS];
                        y = new int[GAME_UNITS];
                        direction = 'R';
                        timer.start();
                        newApple();
                        repaint();
                    }


                }
            }
        }
    }
}
