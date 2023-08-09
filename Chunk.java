import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * represents which tiles in a square section has been changed (in this case, used up) by the player (upon collision)
 * while the world can be simply stored in the seed, the way that the player changes the world must be manually stored
 * this is done in sections, or chunks, which is much more space efficient than loading a giant 2d array
 */
public class Chunk {
  
  public static int chunkSize = 24; //size of chunk. currently 24x24
  public boolean[][] data; //2d array of which tiles have been used up and which haven't
  
  /**
   * Constructs a new Chunk where no tiles have been used up
   */
  public Chunk() {
    // Initialize the data array with default value (false)
    data = new boolean[chunkSize][chunkSize];
    // Loop through entire 2d array
    for (int i = 0; i < chunkSize; i++) {
      for (int j = 0; j < chunkSize; j++) {
        data[i][j] = false;
      }
    }
  }
  
  /**
   * Constructs a new Chunk object with the given data array
   *
   * @param data The data to be stored in the chunk
   */
  public Chunk(boolean[][] data) {
    this.data = data;
  }
  
  /**
   * Saves the chunk data to a file with its name determined by the chunk coordinates
   *
   * @param x The x-coordinate of the chunk
   * @param y The y-coordinate of the chunk
   */
  public void save(int x, int y) {
    try {
      // initialize printwriter with the correct filename
      PrintWriter pw = new PrintWriter(String.format("world/%d_%d", x, y));
      //empty string to append to later
      String out = "";
      for (int i = 0; i < chunkSize; i++) {
        for (int j = 0; j < chunkSize; j++) {
          // loops through the entire chunk. if the tile is used adds a 1 to output string
          if (data[i][j]) {
            out += "1";
          } else {
            // otherwise adds a 0 to output string
            out += "0";
          }
        }
      }
      //write to the file and then save the file by closing printwriter object
      pw.print(out);
      pw.close();
    } catch (IOException e) {
      // Print error message if there's an issue saving the chunk
      System.out.println(String.format("chunk save error at (%d, %d):", x, y));
      System.out.println(e);
    }
  }
  
  /**
   * Loads the chunk data from a file named with the specified coordinates
   *
   * @param x The x-coordinate of the chunk
   * @param y The y-coordinate of the chunk
   * @return The loaded Chunk object
   */
  public static Chunk load(int x, int y) {
    try {
      // opens file for the specific chunk
      Scanner s = new Scanner(new File(String.format("world/%d_%d", x, y)));
      // gets entire file (it's just one line)
      String inRaw = s.nextLine();
      int stringIndex = 0;
      // initialize chunk data array
      boolean[][] data = new boolean[chunkSize][chunkSize];

      for (int i = 0; i < chunkSize; i++) {
        for (int j = 0; j < chunkSize; j++) {
          // loop through array
          char c = inRaw.charAt(stringIndex);
          // fill in with false or true depending on input string
          if (c == '0') {
            data[i][j] = false;
          } else {
            data[i][j] = true;
          }
          // increase index to search in the string
          stringIndex++;
        }
      }
      // close file input stream
      s.close();
      return new Chunk(data);
    } catch (IOException e) {
      // Return a new Chunk object with default data if there's an issue loading the chunk
      return new Chunk();
    }
  }
}