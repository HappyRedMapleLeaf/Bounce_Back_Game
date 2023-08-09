import processing.core.PApplet;

/**
 * Represents the circular player and performs drawing and collision detection + resolution
 * collision detection logic partially taken from:
 * https://www.youtube.com/watch?v=D2a5fHX-Qrs
 */
public class Ball {

  public Vector v;                  // velocity
  public Vector p;                  // position
  private float radius;
  private final float SLOWDOWN_FACTOR = 0.995f;  // The factor by which the ball slows down each frame
  private final float DEAD_ZONE = 0.01f;         // minimum velocity magnitude for the ball to be considered moving
  private PApplet app;              // PApplet instance used for drawing

  /**
   * creates a new Ball object
   *
   * @param app    PApplet instance
   * @param radius radius of the ball
   */
  public Ball(PApplet app, float radius) {
    v = new Vector(0.0f, 0.0f);     // Initialize the velocity to zero
    p = new Vector(0.0f, 0.0f);     // Initialize the position to the origin
    this.app = app;
    this.radius = radius;
  }

  /**
   * updates ball position and velocity upon hitting surface
   *
   * @param near The nearest point on a solid tile to the ball
   */
  public void bounce(Vector near) {
    // algorithm: 2 * (radius - |position - near|) * normalize(position - near)
    p = p.add(p.subtract(near).norm().multScalar(2 * (radius - p.subtract(near).magnitude())));

    // algorithm: -2 * vector projection of velocity on (position - near)
    v = v.add(v.projectOn(p.subtract(near)).multScalar(-2));
  }

  /**
   * Gets the nearest point on a tile to the ball
   *
   * @param boxCoords tile coordinates
   * @return nearest point
   */
  public Vector getNear(Vector boxCoords) {
    // clamps the center of the ball to the tile boundaries, effectively giving 
    return new Vector(
        Math.min(Math.max(p.x, boxCoords.x), boxCoords.x + 1),
        Math.min(Math.max(p.y, boxCoords.y), boxCoords.y + 1)
    );
  }

  /**
   * Checks if the ball is colliding, provided the nearest point on the tile to the ball
   *
   * @param near nearest point on the tile to the ball
   * @return if the ball is colliding with the tile
   */
  public boolean isColliding(Vector near) {
    //colliding if the distance between the point and the center is less than the radius
    return near.subtract(p).magnitude() <= radius;
  }

  /**
   * Slows down the ball's velocity. If the velocity magnitude falls below the dead zone, the velocity is set to zero
   */
  public void slowDown() {
    v = v.multScalar(SLOWDOWN_FACTOR);  // Multiply the velocity by the slowdown factor
    if (v.magnitude() < DEAD_ZONE) {
      v = new Vector(0, 0);  // Set the velocity to zero if below the dead zone
    }
  }

  /**
   * Updates the position of the ball based on its velocity
   */
  public void updatePosition() {
    p = p.add(v);  // adding the velocity vector
  }

  /**
   * Draws the ball on the screen
   *
   * @param width  width of the screen
   * @param height height of the screen
   * @param zoom   zoom factor; pixels per unit
   */
  public void draw(int width, int height, float zoom) {
    app.noStroke(); //disable shape stroke
    drawShadow(10, width, height, zoom);  // recursively draw 10 shadow/trail circles

    // outer circle
    app.fill(70);
    app.ellipse(width / 2, height / 2, radius * 2 * zoom, radius * 2 * zoom);
    // inner circle
    app.fill(20);
    app.ellipse(width / 2, height / 2, radius * zoom, radius * zoom);  
  }

  /**
   * Draws the shadow of the ball recursively.
   *
   * @param n      number of circles left to draw
   * @param width  width of the screen
   * @param height height of the screen
   * @param zoom   zoom factor; pixels per unit
   */
  public void drawShadow(int n, int width, int height, float zoom) {
    // only draw if there are circles left to draw
    if (n > 0) {
      Vector toAdd = v.multScalar(-0.4f * n * zoom); // how much the circle to draw trails behind the player
      app.fill(app.color(209, 167, 255), 50);        // set trail color
      app.ellipse(width / 2 + toAdd.x, height / 2 + toAdd.y, radius * 2 * zoom, radius * 2 * zoom);  // draw the shadow circle
      drawShadow(n - 1, width, height, zoom);        // recursive call to draw the next shadow/trail
    }
  }
}