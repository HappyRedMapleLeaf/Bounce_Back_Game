import processing.core.PApplet;
import processing.core.PImage;

/**
 * A rectangular button that can be drawn and clicked
 */
public class Button {

  // Button rectangle position and size
  int top;
  int left;
  int width;
  int height;
  PApplet app;     // The PApplet instance used for drawing
  private PImage img;      // Button image drawn to screen
  private String function; // The function associated with the button

  /**
   * Creates a new Button object.
   *
   * @param l left coordinate
   * @param t top coordinate
   * @param w width of the button.
   * @param h height of the button.
   * @param i image to be displayed
   * @param a PApplet instance used for drawing
   * @param f button function
   */
  public Button(int l, int t, int w, int h, PImage i, PApplet a, String f) {
    top = t;
    left = l;
    width = w;
    height = h;
    img = i;
    app = a;
    function = f;
  }

  /**
   * Draws the button on the screen
   */
  public void draw() {
    app.image(img, left, top);  // Display the image at the specified position
  }

  /**
   * Handles a mouse click for the button
   *
   * @param mouseX x-coordinate of the mouse click
   * @param mouseY y-coordinate of the mouse click
   * @return Result of the button press, either the fucntion or nothing
   */
  public String click(int mouseX, int mouseY) {
    // Check if the mouse click is within the button bounds
    if (mouseX > left && mouseX < left + width && mouseY > top && mouseY < top + height) {
      return function;  // Return the associated function
    }
    return "";  // Return an empty string otherwise
  }
}