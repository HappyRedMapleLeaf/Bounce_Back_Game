import processing.core.PApplet;
import processing.core.PImage;

/**
 * represents a button that is associated with an upgrade and adds additional
 * functionality to display the upgrade's factor and price
 */
public class UpgradeButton extends Button {
  public Upgrade upgrade;    // upgrade that the button will buy
  private int costX;         // coordinates to display cost data relative to button top left
  private int costY;
  private int factX;         // coordinates to display factor data relative to button top left
  private int factY;
  private float textSize;

  /**
   * Constructs a new UpgradeButton object
   *
   * @param l      left coordinate of button
   * @param t      top coordinate of the button
   * @param w      width of the button
   * @param h      height of the button
   * @param i      image for the button
   * @param a      PApplet object for drawing
   * @param u      button upgrade
   * @param cx     X-coordinate to print cost data
   * @param cy     Y-coordinate to print cost data
   * @param fx     X-coordinate to print factor data
   * @param fy     Y-coordinate to print factor data
   * @param s      size of the text
   */
  public UpgradeButton(int l, int t, int w, int h, PImage i, PApplet a, Upgrade u, int cx, int cy, int fx, int fy, float s) {
    super(l, t, w, h, i, a, "upgrade");
    upgrade = u; costX = cx; costY = cy; factX = fx; factY = fy; textSize = s;
  }

  /**
   * Overrides the draw method of the Button class
   * Draws the button and displays the upgrade's factor and price
   */
  @Override
  public void draw() {
    //draw image
    super.draw();
    app.textSize(textSize);
    app.fill(255);
    app.text(String.format("%.2f", upgrade.factor), factX + left, factY + top);   // display factor at specified coordinates
    app.text(String.format("$%.2f", upgrade.price), costX + left, costY + top);     // display price at specified coordinates
  }
}