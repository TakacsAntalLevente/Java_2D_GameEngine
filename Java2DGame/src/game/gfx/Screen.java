package game.gfx;  // Add this package declaration

import java.util.Arrays;  // Add this import

public class Screen {
    public static final int MAP_WIDTH = 256;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;
    public static final int TILE_SIZE = 32;
    
    private int[] backBuffer;
    public int[] pixels;
    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];
    
    public int width;
    public int height;
    public int xOffset;
    public int yOffset;
    
    public SpriteSheet sheet;

    public Screen(int width, int height, SpriteSheet sheet) {
        this.width = width;
        this.height = height;
        this.sheet = sheet;
        this.pixels = new int[width * height];
        this.backBuffer = new int[width * height];
        
        // Initialize tiles with grass/dirt pattern
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = (i % 16 < 8) ? 0 : 1; // Alternate between two tile types
        }
    }

	public void render() {
        Arrays.fill(backBuffer, 0); // Clear with black
        
        // Calculate visible tile bounds
        int xTileStart = xOffset / TILE_SIZE;
        int yTileStart = yOffset / TILE_SIZE;
        int xTileEnd = (xOffset + width) / TILE_SIZE;
        int yTileEnd = (yOffset + height) / TILE_SIZE;
        
        // Render each visible tile
        for (int yTile = yTileStart; yTile <= yTileEnd; yTile++) {
            for (int xTile = xTileStart; xTile <= xTileEnd; xTile++) {
                renderTile(xTile, yTile);
            }
        }
        
        // Swap buffers
        int[] temp = pixels;
        pixels = backBuffer;
        backBuffer = temp;
    }

    private void renderTile(int xTile, int yTile) {
        int tileIndex = (xTile & MAP_WIDTH_MASK) + (yTile & MAP_WIDTH_MASK) * MAP_WIDTH;
        int tileId = tiles[tileIndex];
        
        // Calculate position in sprite sheet
        int tilesPerRow = sheet.width / TILE_SIZE;
        int sheetX = (tileId % tilesPerRow) * TILE_SIZE;
        int sheetY = (tileId / tilesPerRow) * TILE_SIZE;
        
        // Calculate screen position
        int screenX = xTile * TILE_SIZE - xOffset;
        int screenY = yTile * TILE_SIZE - yOffset;
        
        // Render each pixel in the tile
        for (int y = 0; y < TILE_SIZE; y++) {
            for (int x = 0; x < TILE_SIZE; x++) {
                // Skip if outside screen bounds
                if (screenX + x < 0 || screenX + x >= width || 
                    screenY + y < 0 || screenY + y >= height) {
                    continue;
                }
                
                // Get pixel from sprite sheet
                int sheetPixel = sheetX + x + (sheetY + y) * sheet.width;
                if (sheetPixel >= 0 && sheetPixel < sheet.pixels.length) {
                    backBuffer[(screenX + x) + (screenY + y) * width] = sheet.pixels[sheetPixel];
                }
            }
        }
    }
}