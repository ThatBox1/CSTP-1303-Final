package Flappy;
import javax.swing.*;

public class FlappyBox extends JFrame {
    private GamePanel gamePanel;

    public FlappyBox() {
        initUI();
    }

    private void initUI() {
        setTitle("Flappy Box");
        setSize(800, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new GamePanel();
        add(gamePanel);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlappyBox game = new FlappyBox();
            game.setVisible(true);
        });
    }
}
