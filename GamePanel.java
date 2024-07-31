import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private Box box; 
    private List<Obstacle> obstacles;
    private final int DELAY = 15;
    private final int OBSTACLE_INTERVAL = 2000;
    private long lastObstacleTime;
    private int score; 
    private int highScore; 

    public GamePanel() {
        initGamePanel();
    }

    private void initGamePanel() {
        setBackground(Color.WHITE);
        box = new Box(); 
        obstacles = new ArrayList<>();
        score = 0;
        highScore = 0; 
        addKeyListener(new TAdapter());
        setFocusable(true);
        timer = new Timer(DELAY, this);
        timer.start();
        lastObstacleTime = System.currentTimeMillis();
    }

    // Redraw the panel with the updated position
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
        drawScores(g); 
    }

    // Draws the player's box and obstacles
    private void drawObjects(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.RED);
        g2d.fillRect(box.getX(), box.getY(), box.getWidth(), box.getHeight());

        for (Obstacle obstacle : obstacles) {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(obstacle.getX(), 0, obstacle.getWidth(), obstacle.getGapY()); // Top part
            g2d.fillRect(obstacle.getX(), obstacle.getGapY() + obstacle.getGapHeight(), obstacle.getWidth(), 400 - (obstacle.getGapY() + obstacle.getGapHeight())); // Bottom part
        }
    }

    // Draws the current and high score
    private void drawScores(Graphics g) {
        g.setColor(Color.BLACK);
        Font font = new Font("Times New Roman", Font.BOLD, 20);
        g.setFont(font);

        String scoreText = "Score " + score;
        String highScoreText = "High Score: " + highScore;

        FontMetrics metrics = g.getFontMetrics(font);
        int scoreWidth = metrics.stringWidth(scoreText);
        int highScoreWidth = metrics.stringWidth(highScoreText);

        int panelWidth = getWidth();
        // Display in the center of the panel
        int scoreX = (panelWidth - Math.max(scoreWidth, highScoreWidth)) / 2;
        int scoreY = 30;

        g.drawString(scoreText, scoreX, scoreY);
        g.drawString(highScoreText, scoreX, scoreY + 25); 
    }

    // Updates the game state
    @Override
    public void actionPerformed(ActionEvent e) {
        updateBox(); 
        updateObstacles();
        checkCollisions();
        repaint();
    }

    private void updateBox() {
        box.move(); 
    }

    // Creates new obstacles in an interval using timers
    private void updateObstacles() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastObstacleTime > OBSTACLE_INTERVAL) {
            addObstacle();
            lastObstacleTime = currentTime;
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (obstacle.isVisible()) {
                obstacle.move();
                // when the obstacle reaches the left side, update the score
                if (obstacle.getX() + obstacle.getWidth() < 0) {
                    score++;
                    if (score > highScore) {
                        highScore = score; 
                    }
                }
            } else {
                obstacles.remove(i);
                i--;
            }
        }
    }

    // Set the properties of the obstacles, including the height and gap
    private void addObstacle() {
        Random rand = new Random();
        int gapHeight = 150;
        int obstacleHeight = 50 + rand.nextInt(150);
        int gapY = 400 - obstacleHeight - gapHeight;

        obstacles.add(new Obstacle(800, gapY, 50, obstacleHeight, 5, gapHeight));
    }

    // Checks if the player collides with the obstacles and stops the game if necessary
    private void checkCollisions() {
        for (Obstacle obstacle : obstacles) { 
            if (box.getBounds().intersects(obstacle.getTopBounds()) || 
                box.getBounds().intersects(obstacle.getBottomBounds()) ||
                box.getY() <= 0 || box.getY() >= 400 - box.getHeight()) {
                timer.stop();
                SwingUtilities.invokeLater(() -> {
                    int response = JOptionPane.showConfirmDialog(this, "GAME OVER. \nYour score: " + score + ". \nHigh Score: " + highScore + ". \n\nRESTART?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        restartGame();
                    } else {
                        System.exit(0);
                    }
                });
            }
        }
    }

    // Restarts the game to its initial state
    private void restartGame() {
        box = new Box(); 
        obstacles.clear();
        score = 0; 
        lastObstacleTime = System.currentTimeMillis();
        timer.start();
    }

    // Handles keyboard input to control the player's box
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            box.keyPressed(e);
        }
    }
}

