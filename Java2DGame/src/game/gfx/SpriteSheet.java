package game.gfx;  // This must match your directory structure

// Required imports
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteSheet {
    public final String path;
    public final int width;
    public final int height;
    public final int[] pixels;

    public SpriteSheet(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null) {
            throw new RuntimeException("Failed to load spritesheet: " + path);
        }

        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new int[width * height];
        
        image.getRGB(0, 0, width, height, pixels, 0, width);
    }
}