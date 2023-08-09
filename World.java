import java.util.HashMap;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * represents the game world, including all the building tiles and chunk data
 */
public class World {
  public int seed;                            // number that procedural generation is based on
  private PApplet app;                        // PApplet to draw with
  private HashMap<Integer, Chunk> chunks;     // currently loaded chunks (chunks that can be accessed)
                                              // accessed using one integer which is really a combined x and y coordinate
  // proportions of tiles that are bad tiles, $$$ tiles, wage tiles, and food tiles respectfully
  // all the rest are air
  private final float[] percentages = {0.08f, 0.1f, 0.1f, 0.1f};

  // Images for tiles
  private PImage[] tiles = new PImage[9];
  private PImage[] modifiers = new PImage[4];

  /**
   * Constructs a world with no loaded chunks
   *
   * @param app  PApplet instance
   * @param seed seed for world generation
   */
  public World(PApplet app, int seed) {
    this.seed = seed;
    this.chunks = new HashMap<Integer, Chunk>();
    this.app = app;

    // loads images for buildings and tile modifiers
    for (int i = 0; i < 9; i++) {
      tiles[i] = app.loadImage("graphics/building" + i + ".png");
    }
    modifiers[0] = app.loadImage("graphics/bad.png");
    modifiers[1] = app.loadImage("graphics/food.png");
    modifiers[2] = app.loadImage("graphics/money.png");
    modifiers[3] = app.loadImage("graphics/wage.png");
  }

  /**
   * Saves all the currently loaded chunks into files
   */
  public void save() {
    // loops through every key in the chunk hashmap
    for (int n : chunks.keySet()) {
      // separates the key into the x and y coordinate and then saves to file
      chunks.get(n).save(Convert.oneToFirst(n), Convert.oneToSecond(n));
    }
  }

  /**
   * Loads the chunks within the specified view area from the chunk files
   * or creates a blank chunk if the chunk was never saved before
   *
   * @param x      player x coordinate
   * @param y      player y coordinate
   * @param width  screen width
   * @param height screen height
   * @param zoom   screen zoom factor
   */
  public void loadChunks(float x, float y, int width, int height, float zoom) {
    // calculating the coordinates of the top left chunk that needs to be loaded
    // for the whole view area to be covered
    int l = (int) Math.floor((x - width / 2 / zoom) / Chunk.chunkSize) - 1;
    int w = (int) Math.ceil(width / zoom / Chunk.chunkSize) + 2;
    // how many chunks wide and high the loading area must be
    int t = (int) Math.floor((y - height / 2 / zoom) / Chunk.chunkSize) - 1;
    int h = (int) Math.ceil(height / zoom / Chunk.chunkSize) + 2;
    
    // loops through every specified chunk
    for (int i = l; i < l + w; i++) {
      for (int j = t; j < t + h; j++) {
        // if the chunk is not already loaded, then load the chunk and add it to the hashmap
        if (chunks.get(Convert.twoToOne(i, j)) == null) {
          chunks.put(Convert.twoToOne(i, j), Chunk.load(i, j));
        }
      }
    }

    // removing chunks out of view aread
    ArrayList<Integer> toRemove = new ArrayList<Integer>();

    for (int i : chunks.keySet()) {
      // loops through every chunk that is currently loaded
      // the coordinates of the chunk (key separated into x and y)
      int deletionX = Convert.oneToFirst(i);
      int deletionY = Convert.oneToSecond(i);

      // adds index to removal list if the chunk is outside of new view bounds
      if (deletionX < l || deletionX > l + w || deletionY < t || deletionY > t + h) {
        toRemove.add(i);
      }
    }

    // removes every chunk in the toRemove list
    for (int i : toRemove) {
      int deletionX = Convert.oneToFirst(i);
      int deletionY = Convert.oneToSecond(i);
      // saves the chunk first before removing
      chunks.get(i).save(deletionX, deletionY);
      chunks.remove(i);
    }
  }

  /**
   * Updates a tile to be used. Specifically, sets the array value at the spot in the
   * associated chunk to true
   *
   * @param x The x-coordinate of the tile
   * @param y The y-coordinate of the tile
   */
  public void updateChunk(int x, int y) {
    // get the chunk for the tile by converting tile coordinates to chunk coordinates
    Chunk d = chunks.get(Convert.twoToOne(Convert.chunkCoord(x), Convert.chunkCoord(y)));
    // set the array value in the chunk by converting tile to chunk-tile coordinates
    d.data[Convert.tileCoord(x)][Convert.tileCoord(y)] = true;
  }

  /**
   * Retrieves the tile at the specified coordinates
   *
   * @param x The x-coordinate of the tile
   * @param y The y-coordinate of the tile
   * @return Tile object representing the type of tile
   */
  public Tile getTile(int x, int y) {
    // getting chunk for the tile
    Chunk d = chunks.get(Convert.twoToOne(Convert.chunkCoord(x), Convert.chunkCoord(y)));
    if (d != null) {
      // getting whether or not the tile is used
      if (chunks.get(Convert.twoToOne(Convert.chunkCoord(x), Convert.chunkCoord(y))).data[Convert.tileCoord(x)][Convert.tileCoord(y)]) {
        return Tile.USED;
      }
    }
    
    if (Math.abs(x) < 2 && Math.abs(y) < 2) {
      //clear out a safe area for player at origin
      return Tile.AIR;
    }

    // mapping two numbers into one to feed into the PRNG algorithm from:
    // https://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way
    long A = x >= 0 ? 2 * x : -2 * x - 1;
    long B = y >= 0 ? 2 * y : -2 * y - 1;
    long C = (A >= B ? A * A + A + B : A + B * B) / 2;
    long n = x < 0 && y < 0 || x >= 0 && y >= 0 ? C : -C - 1;

    // change output based on seed
    n *= seed;
    
    //pretty fast prng algorithm
    //from here: https://www.javamex.com/tutorials/random_numbers/xorshift.shtml
    n ^= (n << 21);
    n ^= (n >>> 35);
    n ^= (n << 4);
    
    // some arbitrary conversion from an integer into a float between 0 and 1...
    float m = Math.abs(n % 10000) / 10000f;

    // based on the specified proportions of tiles and the float between 0 and 1,
    // return the tile
    if (m < percentages[0]) {
      return Tile.BAD;
    } else if (m < percentages[0] + percentages[1]) {
      return Tile.MONEY;
    } else if (m < percentages[0] + percentages[1] + percentages[2]) {
      return Tile.INCOME;
    } else if (m < percentages[0] + percentages[1] + percentages[2] + percentages[3]) {
      return Tile.FOOD;
    } else {
      // every other tile is filled with air
      return Tile.AIR;
    }
  }

  /**
   * Draws the world on the screen
   *
   * @param playerX x position
   * @param playerY y position
   * @param width   width of the screen
   * @param height  height of the screen
   * @param zoom    zoom factor
   */
  public void draw(float playerX, float playerY, int width, int height, float zoom) {
    // boundaries of which tiles need to be drawn
    int startX = (int) (Math.floor(playerX) - Math.ceil(width / 2 / zoom));
    int startY = (int) (Math.floor(playerY) - Math.ceil(height / 2 / zoom));
    int endX = (int) (Math.floor(playerX) + Math.ceil(width / 2 / zoom) + 2);
    int endY = (int) (Math.floor(playerY) + Math.ceil(height / 2 / zoom) + 2);

    for (int x = startX; x < endX; x++) {
      for (int y = startY; y < endY; y++) {
        // loop through every tile
        Tile tile = getTile(x, y);
        // screen dimensions of the tile
        float tileLeft = width / 2 + zoom * (x - playerX);
        float tileTop = height / 2 + zoom * (y - playerY);

        if (tile != Tile.AIR) {
          // draws bulding if the tile isn't air
          app.image(tiles[(Math.abs((x + y) * seed) % 8) + 1], tileLeft, tileTop, zoom, zoom);

          // drawing modifiers
          switch (tile) {
            case MONEY:
              // +$$$ overlay
              app.image(modifiers[2], tileLeft, tileTop, zoom, zoom);
              break;
            case INCOME:
              // +WAGE overlay
              app.image(modifiers[3], tileLeft, tileTop, zoom, zoom);
              break;
            case FOOD:
              // +FOOD overlay
              app.image(modifiers[1], tileLeft, tileTop, zoom, zoom);
              break;
            case BAD:
              // -BAD overlay
              app.image(modifiers[0], tileLeft, tileTop, zoom, zoom);
              break;
            default:
              break;
          }
        } else {
          // draw pavement if there's no building
          app.image(tiles[0], tileLeft, tileTop, zoom, zoom);
        }
      }
    }
  }
}