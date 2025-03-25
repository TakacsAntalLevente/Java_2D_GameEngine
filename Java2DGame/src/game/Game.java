package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import game.gfx.Screen;
import game.gfx.SpriteSheet;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 4;
	public static final String NAME = "Game";

	private JFrame frame;

	public boolean running = false;
	public int tickCount = 0;
	//hi

	//private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	//private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	private BufferedImage image;
    private int[] pixels;

	private Screen screen;

	public InputHandler input;

	public Game() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public void init() {
		// Load sprite sheet
		SpriteSheet sheet = new SpriteSheet("/FieldsTile_11.png"); // Update with your actual filename
		
		// Verify sprite sheet loaded correctly
		if (sheet.pixels == null) {
			System.err.println("ERROR: Failed to load sprite sheet!");
			System.exit(1);
		}
		
		screen = new Screen(WIDTH, HEIGHT, sheet);
		input = new InputHandler(this);
		
		// Initialize tiles with more interesting pattern
		for (int i = 0; i < screen.tiles.length; i++) {
			screen.tiles[i] = (i % 8); // Use first 8 tiles from sheet
		}
	}

	public synchronized void start() {
		running = true;
		new Thread(this).start();

	}

	public synchronized void stop() {
		running = false;

	}

	public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;
        double delta = 0;
        
        init();
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            
            while (delta >= 1) {
                tick();
                delta--;
            }
            
            render();
            
            // Cap at 60 FPS
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void render() {
        // Render game to screen's pixel buffer
        screen.render();
        
        // Copy screen pixels to our image buffer
        System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
        
        // Draw to screen with triple buffering
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }


	public void tick() {
		tickCount++;

		if (input.up.isPressed()) {
			screen.yOffset--;
		}
		if (input.down.isPressed()) {
			screen.yOffset++;
		}
		if (input.left.isPressed()) {
			screen.xOffset--;
		}
		if (input.right.isPressed()) {
			screen.xOffset++;
		}

	}

	

	public static void main(String[] args) {
		new Game().start();
	}

}
