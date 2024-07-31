import java.awt.Rectangle;

public class Obstacle {
    private int x, width, height, speed;
    private boolean visible;
    private int gapY, gapHeight;

    public Obstacle(int x, int gapY, int width, int height, int speed, int gapHeight) {
        this.x = x;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.visible = true;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
    }

    public void move() {
        x -= speed;
        if (x + width < 0) {
            visible = false;
        }
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getGapY() {
        return gapY;
    }

    public int getGapHeight() {
        return gapHeight;
    }

    public Rectangle getTopBounds() {
        return new Rectangle(x, 0, width, gapY);
    }

    public Rectangle getBottomBounds() {
        return new Rectangle(x, gapY + gapHeight, width, 400 - (gapY + gapHeight));
    }
}
