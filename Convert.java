/**
 * Provides static methods for conversions between different coordinate systems
 */
public class Convert {
  /**
   * Converts a tile coordinate to a chunk coordinate (which chunk the tile is in)
   *
   * @param n The coordinate to be converted
   * @return The chunk coordinate
   */
  public static int chunkCoord(int n) {
    // Divide the value by the chunk size and return the floor of the result
    return (int) Math.floor((float) n / Chunk.chunkSize);
  }

  /**
   * Converts a tile coordinate to an in-chunk tile coordinate (where within the chunk the tile is in)
   *
   * @param n The coordinate to be converted
   * @return The in-chunk tile coordinate
   */
  public static int tileCoord(int n) {
    // Apply modulo operation with chunk size and ensure positive result
    return (n % Chunk.chunkSize + Chunk.chunkSize) % Chunk.chunkSize;
  }

  /**
   * Combines two numbers into a single value
   *
   * @param num1 The first number
   * @param num2 The second number
   * @return The combined value
   */
  public static int twoToOne(int num1, int num2) {
    // Combine the numbers by bit shifting num1 aside, adding num2, and applying offsets to keep num1 and num2 positive
    // Only works for small values of num1 and num2 but the player would have to go very very far for this system to 
    // start to glitch.
    return (num1 + 0x4000 << 15) + num2 + 0x4000;
  }

  /**
   * Extracts the first number from a combined value
   *
   * @param num3 The combined value
   * @return The first number
   */
  public static int oneToFirst(int num3) {
    // Shift num3 to the right and subtract the offset
    return (num3 >> 15) - 0x4000;
  }

  /**
   * Extracts the second number from a combined value
   *
   * @param num3 The combined value
   * @return The second number
   */
  public static int oneToSecond(int num3) {
    // Apply modulo operation and subtract the offset
    return (num3 % 0x8000) - 0x4000;
  }
}