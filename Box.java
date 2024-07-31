import java.awt.event.KeyEvent;
import java.awt.Rectangle;

public class Box {
    private int x, y, dy;
    private final int JUMP_STRENGTH = -15; 
    private final int GRAVITY = 1;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;

    public Box() {
        x = 50;
        y = 200; 
        dy = 0;
    }

    public void move() {
        dy += GRAVITY;
        y += dy;

        // Ensure the box does not fall below the panel
        if (y > 400 - HEIGHT) {
            y = 400 - HEIGHT;
            dy = 0;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            dy = JUMP_STRENGTH;                                              
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
