package Flappy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Update to the server IP address if needed
    private static final int SERVER_PORT = 12346;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;

    public Client() {
        initNetwork();
        initUI();
    }

    private void initNetwork() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a thread to listen for messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Server response: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        JFrame frame = new JFrame("Game Client");
        gamePanel = new GamePanel(this);
        frame.add(gamePanel);
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Method to send score to the server
    public void sendScore(int score) {
        if (out != null) {
            out.println("Score: " + score);
            System.out.println("Sending score: " + score);
        }
    }

    // Method to send high score to the server
    public void sendHighScore(int highScore) {
        if (out != null) {
            out.println("HighScore: " + highScore);
            System.out.println("Sending high score: " + highScore);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}

// GamePanel class as provided in your previous code
class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private Box box;
    private List<Obstacle> obstacles;
    private final int DELAY = 15;
    private final int OBSTACLE_INTERVAL = 2000;
    private long lastObstacleTime;
    private int score;
    private int highScore;
    private Client client;

    public GamePanel(Client client) {
        this.client = client;
        initGamePanel();
    }
    public GamePanel() {
        this.client = null;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
        drawScores(g);
    }

    private void drawObjects(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.fillRect(box.getX(), box.getY(), box.getWidth(), box.getHeight());

        for (Obstacle obstacle : obstacles) {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(obstacle.getX(), 0, obstacle.getWidth(), obstacle.getGapY());
            g2d.fillRect(obstacle.getX(), obstacle.getGapY() + obstacle.getGapHeight(), obstacle.getWidth(), getHeight() - (obstacle.getGapY() + obstacle.getGapHeight()));
        }
    }

    private void drawScores(Graphics g) {
        g.setColor(Color.BLACK);
        Font font = new Font("Times New Roman", Font.BOLD, 20);
        g.setFont(font);

        String scoreText = "Score: " + score;
        String highScoreText = "High Score: " + highScore;

        FontMetrics metrics = g.getFontMetrics(font);
        int scoreWidth = metrics.stringWidth(scoreText);
        int highScoreWidth = metrics.stringWidth(highScoreText);

        int panelWidth = getWidth();
        int scoreX = (panelWidth - Math.max(scoreWidth, highScoreWidth)) / 2;
        int scoreY = 30;

        g.drawString(scoreText, scoreX, scoreY);
        g.drawString(highScoreText, scoreX, scoreY + 25);
    }

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
                if (obstacle.getX() + obstacle.getWidth() < 0) {
                    score++;
                    if (score > highScore) {
                        highScore = score;
                        client.sendHighScore(highScore); // Notify server of the new high score
                    }
                }
            } else {
                obstacles.remove(i);
                i--;
            }
        }
    }

     private void addObstacle() {
        Random rand = new Random();
        int gapHeight = 150;
        int obstacleHeight = 50 + rand.nextInt(150);
        int gapY = getHeight() - obstacleHeight - gapHeight;

        obstacles.add(new Obstacle(getWidth(), gapY, 50, obstacleHeight, 5, gapHeight));
    }

    private void checkCollisions() {
        for (Obstacle obstacle : obstacles) {
            if (box.getBounds().intersects(obstacle.getTopBounds()) ||
                box.getBounds().intersects(obstacle.getBottomBounds()) ||
                box.getY() <= 0 || box.getY() >= getHeight() - box.getHeight()) {
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

    private void restartGame() {
        client.sendScore(score); // Notify server of the new score
        client.sendHighScore(highScore);
        box = new Box();
        obstacles.clear();
        score = 0;
        lastObstacleTime = System.currentTimeMillis();
        timer.start();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            box.keyPressed(e);
        }
    }
}
